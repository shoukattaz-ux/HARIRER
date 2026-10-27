package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.UserPhotoSource
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted

@Composable
fun UserPhotoDisplay(
    photoSource: UserPhotoSource?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (photoSource == null) {
        Box(
            modifier = modifier
                .background(SurfaceDark)
                .testTag("user_photo_empty"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = contentDescription,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    when (photoSource) {
        is UserPhotoSource.Captured -> {
            Image(
                bitmap = photoSource.bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier.testTag("user_photo_captured")
            )
        }
        is UserPhotoSource.SelectedUri -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoSource.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier.testTag("user_photo_uri")
            )
        }
        is UserPhotoSource.PresetModel -> {
            Image(
                painter = painterResource(id = photoSource.drawableRes),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier.testTag("user_photo_preset")
            )
        }
    }
}
