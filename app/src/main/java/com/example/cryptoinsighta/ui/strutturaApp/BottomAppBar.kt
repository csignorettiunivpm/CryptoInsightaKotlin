package com.example.cryptoinsighta.ui.strutturaApp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cryptoinsighta.navigation.Screen

@Composable
//Il secondo parametro "onNavigate:(String) -> Unit" significa che quando chiamerò sta funzione, dovrò passare una funzione come secondo parametro, che non restituirà nulla (Unit)
fun CreaBottomAppBar(currentRoute: String?, onNavigate:(String) -> Unit){
    Column{
        HorizontalDivider(
            thickness = 3.dp,
            color = Color.LightGray
        )
        NavigationBar(containerColor = MaterialTheme.colorScheme.secondary ){
            NavigationBarItem(
                selected = currentRoute == Screen.Portafoglio.route,
                onClick = { if (currentRoute != Screen.Portafoglio.route) onNavigate(Screen.Portafoglio.route)},
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Portafoglio"
                    )
                },
                label = {Text("Portafoglio")},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Mercato.route,
                onClick = { if (currentRoute != Screen.Mercato.route) onNavigate(Screen.Mercato.route)},
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Mercato"
                    )
                },
                label = {Text("Mercato")},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Storico.route,
                onClick = { if (currentRoute != Screen.Storico.route) onNavigate(Screen.Storico.route)},
                icon = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Storico"
                    )
                },
                label = {Text("Storico")},
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }

}