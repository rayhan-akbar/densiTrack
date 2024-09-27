import base64
import os
import time
import json
import psycopg2
import paho.mqtt.client as mqtt
from ultralytics import YOLO

# Global buffer to store chunks of base64 data
data_buffer = {}

# Function to decode base64 data and save it as an image file
def save_base64_image(base64_string, output_file):
    try:
        image_data = base64.b64decode(base64_string)
        with open(output_file, 'wb') as f:
            f.write(image_data)
        print(f"Image saved as {output_file}")
    except Exception as e:
        print(f"Failed to decode base64 data: {e}")

def insert_data_to_neondb(num_objects, image_filename, device_id):
    try:
        # Connect to the database
        conn = psycopg2.connect(
            host='ep-broad-pine-502933.ap-southeast-1.aws.neon.tech',
            database='Densi',
            user='aflahgmc',
            password='uZtHEC7e3yIo'
        )
        cur = conn.cursor()

        # Step 1: Check if there is already a row for ESPCAM2
        check_query = "SELECT front, back FROM bus WHERE device_id = 'ESPCAM2';"
        cur.execute(check_query)
        result = cur.fetchone()

        if result:
            # Step 2: If the row exists, update the 'front' or 'back' columns
            if device_id == "ESPCAM2":
                update_query = """
                UPDATE bus
                SET front = %s, image = %s, timestamp = %s
                WHERE device_id = %s
                """
                data = (num_objects, image_filename, time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()), "ESPCAM2")
                cur.execute(update_query, data)
            elif device_id == "ESPCAM1":
                update_query = """
                UPDATE bus
                SET back = %s, image = %s, timestamp = %s
                WHERE device_id = %s
                """
                data = (num_objects, image_filename, time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()), "ESPCAM2")
                cur.execute(update_query, data)
        else:
            # Step 3: If no row exists for ESPCAM2, insert a new one for ESPCAM2 (only 'front' gets filled initially)
            if device_id == "ESPCAM2":
                insert_query = """
                INSERT INTO bus (device_id, front, image, timestamp) 
                VALUES (%s, %s, %s, %s)
                """
                data = (device_id, num_objects, image_filename, time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
                cur.execute(insert_query, data)

        # Commit changes to the database
        conn.commit()

        # Close the cursor and connection
        cur.close()
        conn.close()
        print("Data successfully inserted/updated in NeonDB.")
    except Exception as e:
        print(f"An error occurred while inserting into NeonDB: {e}")


# Function to process and decode the accumulated base64 data
def process_buffered_data(device_id):
    global data_buffer
    
    if device_id in data_buffer and len(data_buffer[device_id]) >= 2:
        # Combine the two chunks into one base64 string
        combined_data = ''.join(data_buffer[device_id])
        
        # Clear buffer after combining
        data_buffer[device_id] = []

        # Save and process the combined image
        image_filename = f"received_image_combined_{device_id}.jpg"
        save_base64_image(combined_data, image_filename)

        # Check if the file exists before processing with YOLO
        if not os.path.exists(image_filename):
            print(f"File {image_filename} does not exist!")
            return

        # Start YOLO object detection
        start_time = time.time()
        model = YOLO('yolov9_crowd.pt')
        results = model(image_filename, imgsz=640, conf=0.3, save=True)
        num_objects = len(results[0].boxes)
        end_time = time.time()
        running_time = end_time - start_time

        # Display results
        print(f"Detected objects: {num_objects}")
        print(f"Execution time: {running_time:.2f} seconds")

        # Insert detection result into NeonDB
        insert_data_to_neondb(num_objects, image_filename, device_id)

# MQTT callback when a message is received
def on_message(client, userdata, message):
    print(f"Received message on topic {message.topic}")
    
    try:
        # Decode payload from MQTT message
        payload_str = message.payload.decode('utf-8')
        payload = json.loads(payload_str)  # Parse JSON payload
        
        # Extract fields from JSON
        device_id = payload.get('deviceId', 'unknown_device')
        base64_image_data = payload.get('payload', '')

        if not base64_image_data:
            print("No base64 image data found in the payload.")
            return

        # Add the incoming base64 data to the buffer for the specific deviceId
        if device_id not in data_buffer:
            data_buffer[device_id] = []

        print(device_id)
        
        data_buffer[device_id].append(base64_image_data)

        # Process the data if two chunks have been received
        process_buffered_data(device_id)

    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}")
    except Exception as e:
        print(f"Error processing message: {e}")

# MQTT setup
broker = "ab25c1e854d343e386319e43604e5926.s1.eu.hivemq.cloud"
port = 8883
topic = "Camera"
username = "despro-IOT"
password = "P@ssw0rd"

client = mqtt.Client()
client.username_pw_set(username, password)
client.tls_set(ca_certs="certificate.pem")
client.on_message = on_message

# Connect to broker and subscribe to topic
client.connect(broker, port)
client.subscribe(topic)
client.loop_forever()
