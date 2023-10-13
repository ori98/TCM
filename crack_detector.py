import cv2
from PIL import Image
import numpy as np
from tensorflow.keras.models import load_model

def predict_crack (image_path, model_path='vgg19_crack.h5'):
    # Load the VGG19 model
    model = load_model(model_path)

    # Load and preprocess a single image for prediction
    img = cv2.imread(image_path)
    img = Image.fromarray(img)
    img = img.resize((240, 240))
    img_array = np.array(img)
    img_array = img_array.astype('float32') / 255  # Apply the same rescaling as during training
    img_array = np.expand_dims(img_array, axis=0)  # Add a batch dimension

    # Make predictions using your VGG19 model
    predictions = model.predict(img_array)

    # Apply the binary classification threshold (0.5) to make a prediction
    if predictions >= 0.5:
        prediction_label = "Crack Detected"
    else:
        prediction_label = "Healthy Tongue"

    return prediction_label

if __name__ == "__main__":
    image_path = 'crack_tongue.jpeg'  # Change to the path of your image
    prediction = predict_image_class(image_path)
    print(f"Result: {prediction}")
