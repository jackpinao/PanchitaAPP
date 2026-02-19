package com.pinao.panchitaapp.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pinao.panchitaapp.R
import com.pinao.panchitaapp.presentation.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    route: String,
    modifier: Modifier = Modifier,
    navigationToHome: () -> Unit = {},
    navigationToRecarga: () -> Unit = {},
    navigationToGuiaRemision: () -> Unit = {},
    navigationToAddProduct: () -> Unit = {},
    onLogout: () -> Unit = {},
    closeDrawer: () -> Unit = {},
) {
    ModalDrawerSheet(
        modifier = Modifier
    ) {
        DrawerHeader(modifier)
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.home),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = route == AppScreens.Home.route,
            onClick = {
                closeDrawer()
                navigationToHome()
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.outline_add_home_24),
                    contentDescription = stringResource(id = R.string.home)
                )
            },
            shape = MaterialTheme.shapes.small
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.recarga),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = route == AppScreens.Recarga.route,
            onClick = {
                closeDrawer()
                navigationToRecarga()
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_sticky_note_2_24),
                    contentDescription = stringResource(id = R.string.recarga)
                )
            },
            shape = MaterialTheme.shapes.small
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.guia_remision),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = route == AppScreens.GuiaRemision.route,
            onClick = {
                closeDrawer()
                 navigationToGuiaRemision()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_sticky_note_2_24),
                    contentDescription = stringResource(id = R.string.guia_remision)
                )
            },
            shape = MaterialTheme.shapes.small
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.add_produdct),
                    style = MaterialTheme.typography.labelSmall
                )

            },
            selected = route == AppScreens.AddProduct.route,
            onClick = {
                closeDrawer()
                navigationToAddProduct()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = stringResource(id = R.string.add_produdct)
                )
            },
            shape = MaterialTheme.shapes.small
        )

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        NavigationDrawerItem(
            label = {
                Text(
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = false,
            onClick = {
                closeDrawer()
                onLogout()
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(id = R.string.logout)
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun DrawerHeader(modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(dimensionResource(id = R.dimen.header_padding))
            .fillMaxWidth()
    ) {
        Image(
            painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(dimensionResource(id = R.dimen.header_image_size))
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))
        Text(
            text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
fun PreviewDrawerHeader() {
    AppDrawer(
        route = AppScreens.Home.route,
    )
}
