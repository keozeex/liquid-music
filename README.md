# پخش‌کننده شیشه‌ای (Liquid Glass Music Player)

اپلیکیشن اندرویدی native (Kotlin) با طراحی شیشه‌ای مایع، نئون ملایم و حس گاتهامی،
که موزیک‌های ذخیره‌شده روی گوشی را خودکار اسکن و پخش می‌کند.

## امکانات

- **اسکن خودکار حافظه گوشی**: با `MediaStore` تمام فایل‌های صوتی حافظه‌ی داخلی و کارت حافظه
  را بدون اینترنت یا سرور پیدا می‌کند (`data/MusicScanner.kt`).
- **پخش محلی**: با `ExoPlayer` (Media3)، شامل پخش در پس‌زمینه و کنترل از نوتیفیکیشن.
- **جست‌وجو**: فیلتر زنده روی عنوان، هنرمند و آلبوم.
- **رابط کاربری شیشه‌ای (Liquid Glass)**: کارت‌ها و دکمه‌های نیمه‌شفاف با لبه‌ی نازک نئون،
  قاب جلد آلبوم با افکت شیشه و نوار پیشرفت موج‌دار درخشان.
- **پشتیبانی کامل فارسی**: راست‌به‌چپ (`supportsRtl="true"`)، فونت وزیرمتن، و تمام متن‌های
  پیش‌فرض اپ به فارسی (با نسخه‌ی انگلیسی جایگزین در `values-en`).
- **ریسپانسیو**: تمام صفحات با `ConstraintLayout` و درصد عرض/ارتفاع نسبی ساخته شده‌اند،
  نه سایزهای ثابت، تا روی هر اندازه گوشی درست نمایش داده شوند.

## ساختار پروژه

```
app/src/main/java/com/liquidglass/musicplayer/
├── data/
│   ├── Track.kt              مدل داده‌ی آهنگ
│   └── MusicScanner.kt       اسکنر خودکار MediaStore
├── service/
│   ├── PlaybackService.kt    سرویس پخش پس‌زمینه (Media3 Session)
│   └── PlayerHolder.kt       نگه‌دارنده‌ی ExoPlayer برای دسترسی سریع در UI
├── ui/
│   ├── SplashActivity.kt
│   ├── MainActivity.kt       کتابخانه + اسکن + جست‌وجو + مینی‌پلیر
│   ├── NowPlayingActivity.kt صفحه‌ی پخش شیشه‌ای
│   └── TrackAdapter.kt
└── util/
    └── PermissionUtil.kt     مدیریت مجوز اندروید 13+ در برابر قدیمی‌تر
```

## راه‌اندازی در Android Studio

1. این پوشه را در Android Studio باز کن (Open an existing project).
2. صبر کن Gradle Sync کامل شود (نیاز به اینترنت برای دانلود dependency ها دارد).
3. **فونت فارسی را دانلود کن** (به دلیل محدودیت محیط، فایل‌های .ttf همراه پروژه نیستند):
   - برو به: https://fonts.google.com/specimen/Vazirmatn
   - فایل‌های زیر را دانلود و داخل `app/src/main/res/font/` کپی کن:
     - `vazirmatn_regular.ttf`
     - `vazirmatn_medium.ttf`
     - `vazirmatn_semibold.ttf`
     - `vazirmatn_bold.ttf`
   - (فایل `font/vazirmatn.xml` از قبل به این نام‌ها اشاره می‌کند، فقط فایل‌ها را اضافه کن)
4. روی یک گوشی یا شبیه‌ساز اندروید (API 24+) اجرا کن.
5. هنگام اولین اجرا، روی «اسکن خودکار حافظه گوشی» بزن و مجوز دسترسی به فایل‌های صوتی را بده.

## نکات مهم برای ادامه‌ی توسعه

- **آیکون اپ**: مسیرهای `mipmap/ic_launcher` فعلاً placeholder هستند؛ از Android Studio
  Image Asset Studio (راست‌کلیک روی `res` → New → Image Asset) برای ساخت آیکون نهایی استفاده کن.
- **اتصال نوتیفیکیشن**: `PlaybackService` با Media3 `MediaSessionService` ساخته شده؛
  برای کنترل کامل از نوتیفیکیشن/لاک‌اسکرین، `PlayerHolder` را به `MediaController` متصل به
  این سرویس مهاجرت بده (در حال حاضر `PlayerHolder` یک نمونه‌ی مستقیم ExoPlayer در پراسس اصلی
  نگه می‌دارد که برای تست و نسخه‌ی اول کافی است).
- **پلی‌لیست/آلبوم/هنرمند**: `MusicScanner.scanAlbums()` و `scanArtists()` از قبل گروه‌بندی
  را انجام می‌دهند؛ فقط باقی است صفحات `AlbumsFragment` / `ArtistsFragment` مشابه `MainActivity`
  ساخته شوند (تب‌های پایین از قبل در `activity_main.xml` طراحی شده‌اند: `navAlbums`, `navArtists`, `navPlaylists`).
- **رنگ پویا از روی جلد آلبوم**: dependency `androidx.palette:palette-ktx` از قبل اضافه شده؛
  می‌توانی با `Palette.from(bitmap).generate()` رنگ غالب جلد آلبوم را بگیری و به‌صورت پویا
  به هاله‌های پس‌زمینه‌ی `NowPlayingActivity` بدهی (دقیقاً همان «واکنش پس‌زمینه به رنگ آلبوم»
  که در طراحی خواسته شده).

## مجوزها (Permissions)

- `READ_MEDIA_AUDIO` (اندروید ۱۳ به بالا) یا `READ_EXTERNAL_STORAGE` (قدیمی‌تر)
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` برای پخش در پس‌زمینه
- `POST_NOTIFICATIONS` برای کنترل از نوتیفیکیشن

تمام این‌ها به‌صورت runtime و با متن فارسی در `strings.xml` مدیریت می‌شوند.
