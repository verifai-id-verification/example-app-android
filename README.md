# Verifai SDK Android example app

This repository contains two example implementations (written in Kotlin and
Java) of the Verifai Android SDK. The Verifai SDK can be used to verify the
identity of users. This can be done by scanning passports or other
identification documents with the camera of the Android device. This in
combination with on device optical character recognition (OCR).

The latest documentation that describes the use of this SDK can be found over
here: [docs](https://docs.verifai.com/sdk/android/). Please feel free to submit
issues, pull requests and/or
[contact our support](https://www.verifai.com/en/support/).

The "Android" name is property of
[Google LLC](https://developer.android.com/legal).

Verifai Copyright © | All rights reserved | The Verifai terms and conditions
apply: [https://www.verifai.com/en/terms-and-conditions/](https://www.verifai.com/en/terms-and-conditions/)

## Get started

A Verifai license is needed to make the examples work.
Request a license at [https://dashboard.verifai.com](https://dashboard.verifai.com).
The license string has to be converted to the right format for gradle.
The `create_license_string.sh` can be used to do this.

```bash
./create_license_string "my license string from the dashboard"
```

Put the output license in a file called `secret.properties` and set the verifaiLicense property:

```gradle
verifaiLicense="=== Verifai Licence file V2 ===\\n" +\
"...\\n" +\
"..."
```

This file should be located in the same folder as the top level `build.gradle` file.
It is ignored by version control.
