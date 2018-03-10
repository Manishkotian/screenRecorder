# screenRecorder
Android application to detect any screen recorder application is running or not for security purpose.
Using UsageStatsManager we get application running and its last time used from Android for specific given time, this UsageStatsManager works only for android greater then LOLLIPOP. 
We check the package name got from UsageStatsManager with installed application in android and if it matches then we store it in array.
Then we check that array of package names with the server packages sent by server, if it matches then we check if last time used of that package name is greater then current system time.
If its greater then we detect that screen recorder app is running currently.
