import cv2
from ultralytics import YOLO

# Load the model
model = YOLO('yolov8n_custom.pt')

# Initialize webcam
cap = cv2.VideoCapture(0)  # 0 is the default ID for the primary webcam

while True:
    # Capture frame-by-frame
    ret, frame = cap.read()

    if not ret:
        break  # Exit the loop if there's an issue with webcam feed

    # Predict on the current frame
    results = model(frame, imgsz=640, conf=0.3)

    # Filter detected objects with confidence score greater than 0.50
    filtered_boxes = [box for box in results[0].boxes if box.conf > 0.50]

    # Calculate and display the number of objects detected
    num_objects = len(filtered_boxes)
    print(f"Jumlah objek yang terdeteksi: {num_objects}")

    # Display the resulting frame with predictions
    annotated_frame = results[0].plot()  # This plots the predictions on the frame
    cv2.imshow('Webcam', annotated_frame)

    # Exit loop if 'q' is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the capture and close the window
cap.release()
cv2.destroyAllWindows()


