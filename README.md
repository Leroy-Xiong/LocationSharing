# LocShare

Our group’s app product is called LocShare. LocShare aims to provide users with a convenient platform to share and update their current locations with friends and family. With this app, users can effortlessly connect and stay informed about the whereabouts of their loved ones in real-time.
  
## Installation  
  
1. Make sure you have Android Studio (2023.2) installed.  
2. Unzip `LocShare.zip`
3. Open the project in Android Studio.  
4. Build (Gradle 8.2) and run the app.  
  
## Usage  

At first, users can register and use an account labeled with an email address on LocShare's login and registration page. Once logged in, users can enter the location they want to share in the form of longitude and latitude on the location entry page, along with their name, so that other users will know who belongs to that coordinate. If users don't know the exact latitude and longitude of the location they want to share, they can also search for their location on the map, which supports both Chinese and English. In the same interface, users can also get their current location, which requires LocShare to have access to the phone's fine Positioning permission. In addition, users can drag the location marker on the map to fine-tune their location.

After entering the location you want to share, you can see your location (blue marker) and other users' locations (red markers) on the same map. When clicking on any of the markers, you can see the user's name and the exact latitude and longitude of that marker. In the Settings page, you can turn off the display of your own location, which prevents all other users from seeing that user's location. Users can also change the style of the map to satellite view or modify their location.
  
## Technology Stack and Dependencies  
  
- Java
- implementation("com.google.android.gms:play-services-maps:18.2.0")
- implementation("com.google.android.gms:play-services-location:21.2.0")
- implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
- implementation("com.google.firebase:firebase-firestore")

