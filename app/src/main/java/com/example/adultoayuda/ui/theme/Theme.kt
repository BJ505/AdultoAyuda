package com.example.adultoayuda.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

//<color name="AAGray">#50595f</color>
//<color name="AABlue">#004cf2</color>
//<color name="AAOrange">#fba536</color>
//<color name="AARed">#ff5362</color>
//<color name="AAPurple">#6319c3</color> rgb(99, 25, 195)
//<color name="AABackground">#f1f2f3</color>
private val DarkColorScheme = darkColorScheme(
    primary =  Color(0, 76, 242),
    secondary = Color(251, 165, 54),
    tertiary = Color(255, 83, 98)
)

private val LightColorScheme = lightColorScheme(
    primary =  Color(0, 76, 242),
    secondary = Color(251, 165, 54),
    tertiary = Color(255, 83, 98)

    /* Other default colors to override
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),*/

)

@Composable
fun AdultoAyudaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
            )),
        content = content
    )
}