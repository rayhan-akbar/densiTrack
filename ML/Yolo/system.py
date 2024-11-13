import base64
import os
import time
import psycopg2
import paho.mqtt.client as mqtt
from ultralytics import YOLO

# Function to decode base64 data and save it as an image file
def save_base64_image(base64_string, output_file):
    try:
        image_data = base64.b64decode(base64_string)
        with open(output_file, 'wb') as f:
            f.write(image_data)
        print(f"Image saved as {output_file}")
    except Exception as e:
        print(f"Failed to decode base64 data: {e}")

# Function to insert the detected object count into NeonDB,
def insert_data_to_neondb(num_objects, image_filename):
    try:
        conn = psycopg2.connect(
            host='ep-broad-pine-502933.ap-southeast-1.aws.neon.tech',
            database='Densi',
            user='aflahgmc',
            password='uZtHEC7e3yIo'
        )
        cur = conn.cursor()
        insert_query = """
        INSERT INTO bus (detected_objects, image, timestamp) 
        VALUES (%s, %s, %s)
        """
        data = (num_objects, image_filename, time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
        cur.execute(insert_query, data)
        conn.commit()
        cur.close()
        conn.close()
        print("Data successfully inserted into NeonDB.")
    except Exception as e:
        print(f"An error occurred while inserting into NeonDB: {e}")

# MQTT callback when a message is received
def on_message(client, userdata, message):
    print(f"Received message on topic {message.topic}")
    
    try:
        base64_image_data = message.payload.decode('latin1')  # Decode to handle binary data correctly
        image_filename = "received_image.jpg"
        save_base64_image(base64_image_data, image_filename)

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
        insert_data_to_neondb(num_objects, image_filename)

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
