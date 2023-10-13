from crack_detector import predict_crack

if __name__ == "__main__":
    image_path = 'healthy_tongue.jpeg'  # Change to the path of the image you want to test
    prediction = predict_crack(image_path)
    print(f"Input image: {image_path}")
    print(f"Result: {prediction}")

