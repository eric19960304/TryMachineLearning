# edge-to-shoe-android-app

An Android App demonstrating edge2shoe in machine learning papers MUNIT: Multimodal UNsupervised Image-to-image Translation (https://github.com/NVlabs/MUNIT)

Require back-end server that running the code in my forked MUNIT repo (see: https://github.com/eric19960304/MUNIT)

Steps:

1. follow the instruction in the README file of my forked repo to setup the enviroment

2. run the nodejs server in my forked MUNIT repo

3. modify the line no. 117 in `/app/src/main/java/hku/com3330/trymachinelearning/MainActivity.java`: 
from `String url = "http://10.21.4.106:8890/edge2Shoe";` to `String url = "http://[your server ip]/edge2Shoe";`, where [your server ip] should be accessible from your andorid phone/emulator.

4. install the apk or run this project using Android Studio's emulator to try the image translation service.
