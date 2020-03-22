
# How to contribute & build *Points Buraco*

## Before you import the project to Android Studio:

- Make sure you have latest Android Studio version.
- Set the following Android Studio Settings (to ensure same code formatting):

> New line: CRLF - Encoding: UTF-8 - Indenting: 4 spaces
 

## After above steps:

- Fork the project
- Clone it to your desktop
- Import the project in Android Studio
- Add Firebase API keys (follow the next section)
- Let it build & Start coding

### Adding the required files for Firebase and Ads
1. Visit https://console.firebase.google.com/ and create a new Android project
2. Download the google-services.json file and place it in /app
3. Visit https://apps.admob.com/ and add a new app
4. Copy the app ID and pub ID
5. Create a file secret_id.xml in the /app/main/res/strings folder
6. Use this template
<?xml version="1.0" encoding="utf-8"?>
```html
<resources>
    <string name="admob_app_id" translatable="false">app_id_copied_from_Admob</string>
    <string name="admob_pub_id" translatable="false">pub_id_copied_from_Adomob_account_settings</string>
    <string name="admob_2players_unit_id" translatable="false">@string/test_ad_unit_id</string>
    <string name="admob_3players_unit_id" translatable="false">@string/test_ad_unit_id</string>
</resources>
```
7. Save all and you are ready

## Submitting Pull Request

> Please make sure your commit messages are meaningful.
 
- Create new Branch with the feature or fix you made.
- Submit your PR with an explanation of what you did & why

> I really appreciate your efforts on contributing to this project.


# Versioning

Just some information about how I manage the app version.

Note that first releases (from 1.0 to 2.8) were not following this pattern.

> Major.minor.fix

**Fix**: this number is related to a bug fix, a correction, polishing, or a minimal change. 

**Minor**: this number is related to a new feature. Every new feature or interface element increase this number.

**Major**: this number is increased when a new concept of the app is released, with a big rework of the core of the app