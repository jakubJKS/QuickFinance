package com.example.loginsignupsql.ItemActivities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.loginsignupsql.R
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme
import kotlinx.coroutines.launch
import java.io.OutputStream

class Product2Activity : ComponentActivity() {
    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()

        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    PdfGeneratorScreen()
                }
            }
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.dark_gray)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkPermissions()) {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions Denied..", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun PdfGeneratorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PDF Generator",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (activity?.let { checkPermissions(it) } == true) {
                        scope.launch {
                            generatePDF(context)
                        }
                    } else {
                        activity?.let { requestPermissions(it) }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Generate PDF")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
suspend fun generatePDF(context: Context) {
    Log.d("PDFGenerator", "Starting PDF generation")

    val sharedPreferences = context.getSharedPreferences("com.example.loginsignupsql.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("USERNAME", "N/A") ?: "N/A"
    val fullname = sharedPreferences.getString("FULLNAME", "N/A") ?: "N/A"
    val email = sharedPreferences.getString("EMAIL", "N/A") ?: "N/A"
    val phone = sharedPreferences.getString("PHONE", "N/A") ?: "N/A"
    val address = sharedPreferences.getString("ADDRESS", "N/A") ?: "N/A"

    // Add logging to verify shared preferences content
    Log.d("PDFGenerator", "Username: $username")
    Log.d("PDFGenerator", "Full Name: $fullname")
    Log.d("PDFGenerator", "Email: $email")
    Log.d("PDFGenerator", "Phone: $phone")
    Log.d("PDFGenerator", "Address: $address")

    val pdfDocument = PdfDocument()
    val paint = Paint()

    val pageInfo = PdfDocument.PageInfo.Builder(792, 1120, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    // Draw the header "QUICKFINANCE" at the top center of the PDF
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 36f
    paint.isFakeBoldText = true
    val headerX = pageInfo.pageWidth / 2f
    val headerY = 50f // Adjust the Y position as needed

    // Draw "QUICK" in yellow
    paint.color = android.graphics.Color.YELLOW
    canvas.drawText("QUICK", headerX - paint.measureText("FINANCE") / 2, headerY, paint)

    // Draw "FINANCE" in black
    paint.color = android.graphics.Color.BLACK
    canvas.drawText("FINANCE", headerX + paint.measureText("QUICK") / 2, headerY, paint)

    // Reset paint settings for user information
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 14f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.BLACK

    // Draw user information on the PDF
    canvas.drawText("Username: $username", 100f, 100f, paint)
    canvas.drawText("Full Name: $fullname", 100f, 120f, paint)
    canvas.drawText("Email: $email", 100f, 140f, paint)
    canvas.drawText("Phone: $phone", 100f, 160f, paint)
    canvas.drawText("Address: $address", 100f, 180f, paint)

    // Draw the logo at the bottom right corner with reduced size
    val logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
    val scaledLogo = Bitmap.createScaledBitmap(logo, 50, 50, false) // Reduce the size of the logo
    val logoX = pageInfo.pageWidth - scaledLogo.width - 50f
    val logoY = pageInfo.pageHeight - scaledLogo.height - 50f
    canvas.drawBitmap(scaledLogo, logoX, logoY, paint)
    Log.d("PDFGenerator", "Logo drawn at: x=$logoX, y=$logoY")

    pdfDocument.finishPage(page)

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "QuickFinance.pdf") // Change the filename here
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
    }

    val uri: Uri? = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
    uri?.let {
        try {
            val outputStream: OutputStream? = resolver.openOutputStream(it)
            pdfDocument.writeTo(outputStream)
            outputStream?.close()
            Log.d("PDFGenerator", "PDF file generated successfully.")
            Toast.makeText(context, "PDF file generated..", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("PDFGenerator", "Failed to generate PDF file.", e)
            Toast.makeText(context, "Failed to generate PDF file..", Toast.LENGTH_SHORT).show()
        }
    } ?: run {
        Log.e("PDFGenerator", "Failed to create URI.")
        Toast.makeText(context, "Failed to create URI..", Toast.LENGTH_SHORT).show()
    }

    pdfDocument.close()
}


fun checkPermissions(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

fun requestPermissions(activity: Activity) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    ActivityCompat.requestPermissions(activity, permissions, 101)
}
