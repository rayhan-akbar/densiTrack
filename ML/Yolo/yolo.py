from ultralytics import YOLO
from matplotlib import pyplot as plt
import cv2

# Load the model
model = YOLO('yolov10x_custom.pt')

# Predict on the image with the specified parameters
results = model('latihan.jpg', imgsz=640, conf=0.3, save=True)

# Filter detected objects with confidence score greater than 0.50
filtered_boxes = [box for box in results[0].boxes if box.conf > 0.50]

# Calculate and display the number of objects detected with high confidence
num_objects = len(filtered_boxes)
print(f"Jumlah objek yang terdeteksi dengan akurasi lebih dari 0.50: {num_objects}")

# Load the saved image
result_image_path = 'runs/detect/predict7/latihan.jpg'  # Adjust the path as necessary
result_image = cv2.imread(result_image_path)

# Convert from BGR to RGB
result_image = cv2.cvtColor(result_image, cv2.COLOR_BGR2RGB)

# Display the image using matplotlib
plt.imshow(result_image)
plt.axis('off')  # Hide axis
plt.show()
