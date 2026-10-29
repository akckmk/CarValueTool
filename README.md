# Car Value Tool

An Android app for estimating a used car's fair market value, in the spirit of Kelley Blue Book. Enter the year, make, model, trim, mileage, condition, wear and tear, and history to get:

- **Trade-in value**: a typical dealer offer
- **Private party value**: what you could expect selling to another person
- **Dealer retail value**: a typical asking price on a dealer lot

Each value comes with a low–high range, a confidence level, and an itemized breakdown showing how each factor moved the price.

## Privacy

- **Nothing is saved.** Entries and results live only in memory and are gone when the app closes. The app uses no database, preferences or cache files.
- **No network access.** The app does not request the `INTERNET` permission. All calculations run on the device.
- **No backups.** Android cloud backup and device transfer are turned off.
- **Export only when you ask.** "Save PDF" and "Save text" open the system *Save as* dialog, so the report goes to a location you pick (for example, Downloads) and the app keeps no copy.

## What affects the value

| Factor | Effect |
| --- | --- |
| Year / age | Depreciation curve by segment (economy, luxury, pickup, EV, and so on), adjusted for how well the brand holds value |
| Trim level | Base, mid, upper or top trim, applied to the MSRP |
| Mileage | Compared with expected miles for the vehicle's age and segment, plus extra penalties above 150k and 200k miles |
| Condition | Excellent, Very Good, Good, Fair or Poor |
| Wear & tear | Scratches, dents, rust, paint, windshield, interior, odor, tires, brakes, warning lights, mechanical and electrical problems (a percentage deduction or a fixed repair cost) |
| History | Accidents, title status (clean, rebuilt, lemon, salvage), number of owners, service records |
| Other | Drivetrain, transmission, color and region |

The built-in catalog covers about 30 makes and more than 200 models sold in the US from 1990 to today.

> Values are estimates from an offline depreciation model, not live market data, and the app is not affiliated with Kelley Blue Book.

## Project layout

```
core/   Pure Kotlin valuation engine, vehicle catalog and report builder (unit tested, no Android dependencies)
app/    Android app (Jetpack Compose, Material 3), including PDF and text export
```

## Building

Requirements: JDK 17 and the Android SDK (API 35). Android Studio provides both.

```bash
./gradlew :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`. To install it on a phone with USB debugging turned on:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

To run the valuation engine tests without the Android SDK:

```bash
./gradlew -PcoreOnly :core:test
```

### Download a prebuilt APK

Every push runs the **Android build** GitHub Actions workflow, which runs the tests and uploads the debug and release APKs as the `car-value-tool-apk` artifact. Download it from the workflow run, copy the APK to your phone, and open it. You may need to allow installs from unknown sources. The release APK is signed with a debug key for convenience, so set up your own signing key before you publish to the Play Store.

Requires Android 8.0 (API 26) or later.
