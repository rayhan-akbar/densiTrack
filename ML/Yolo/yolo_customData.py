import time
from ultralytics import YOLO

# Start the timer
start_time = time.time()

# Load the model
model = YOLO('yolov9_crowd_latest.pt')

# Predict on the image with the specified parameters                         
results = model('nyoba2.jpg', imgsz=640, conf=0.3, save=True)

# Calculate the number of detected objects
num_objects = len(results[0].boxes)  # results[0] refers to the first image prediction

# End the timer
end_time = time.time()

# Calculate running time
running_time = end_time - start_time

# Display the results
print(f"Jumlah objek yang terdeteksi: {num_objects}")
print(f"Waktu eksekusi: {running_time:.2f} detik")
