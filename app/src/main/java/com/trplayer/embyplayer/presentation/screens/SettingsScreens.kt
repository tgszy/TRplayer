package com.trplayer.embyplayer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * 服务器设置界面
 * 用于添加、编辑和管理Emby服务器连接
 */
@Composable
fun ServerSettingsScreen(navController: NavHostController) {
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var serverName by remember { mutableStateOf("") }
    var isConnecting by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "服务器设置",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            // 服务器连接卡片
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "添加新服务器",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 服务器名称
                    OutlinedTextField(
                        value = serverName,
                        onValueChange = { serverName = it },
                        label = { Text("服务器名称（可选）") },
                        placeholder = { Text("例如：家庭服务器") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Storage, contentDescription = "服务器名称")
                        }
                    )
                    
                    // 服务器地址
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("服务器地址") },
                        placeholder = { Text("例如：https://your-server.com") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Language, contentDescription = "服务器地址")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                    )
                    
                    // 用户名
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("用户名") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "用户名")
                        }
                    )
                    
                    // 密码
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("密码") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "密码")
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    
                    // 连接按钮
                    Button(
                        onClick = {
                            isConnecting = true
                            // 这里将实现服务器连接逻辑
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = serverUrl.isNotBlank() && username.isNotBlank() && password.isNotBlank() && !isConnecting
                    ) {
                        if (isConnecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("连接中...")
                        } else {
                            Text("连接服务器")
                        }
                    }
                }
            }
        }
        
        item {
            // 已保存的服务器列表
            Text(
                text = "已保存的服务器",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 这里将显示已保存的服务器列表
        // 暂时显示占位内容
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "暂无已保存的服务器",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "请先添加并连接一个服务器",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        item {
            // 缓存管理设置
            CacheManagementSettingsCard(
                onNavigateToCacheManagement = {
                    navController.navigate("cache_management")
                }
            )
        }
    }
}

/**
 * 缓存管理设置卡片
 */
@Composable
fun CacheManagementSettingsCard(
    onNavigateToCacheManagement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "缓存管理",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "管理应用缓存，清理不需要的文件以释放存储空间",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onNavigateToCacheManagement,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("管理缓存")
            }
        }
    }
}

/**
 * 服务器连接测试对话框
 */
@Composable
fun ServerConnectionDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    connectionStatus: ConnectionStatus
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("服务器连接测试")
            },
            text = {
                when (connectionStatus) {
                    ConnectionStatus.CONNECTING -> Text("正在连接服务器...")
                    ConnectionStatus.SUCCESS -> Text("服务器连接成功！")
                    ConnectionStatus.ERROR -> Text("服务器连接失败，请检查设置")
                }
            },
            confirmButton = {
                when (connectionStatus) {
                    ConnectionStatus.SUCCESS -> {
                        Button(onClick = onDismiss) {
                            Text("确定")
                        }
                    }
                    ConnectionStatus.ERROR -> {
                        Row {
                            Button(onClick = onDismiss) {
                                Text("取消")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = onRetry) {
                                Text("重试")
                            }
                        }
                    }
                    ConnectionStatus.CONNECTING -> {
                        // 连接中时不显示按钮
                    }
                }
            }
        )
    }
}

/**
 * 服务器连接状态枚举
 */
enum class ConnectionStatus {
    CONNECTING,
    SUCCESS,
    ERROR
}

/**
 * 服务器列表项组件
 */
@Composable
fun ServerListItem(
    serverName: String,
    serverUrl: String,
    isConnected: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = serverName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = serverUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // 连接状态
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (isConnected) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.error,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "已连接" else "未连接",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // 操作按钮
            Row {
                IconButton(onClick = onConnect) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Link else Icons.Default.LinkOff,
                        contentDescription = if (isConnected) "断开连接" else "连接"
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                }
            }
        }
    }
}

/**
 * 服务器编辑对话框
 */
@Composable
fun ServerEditDialog(
    isVisible: Boolean,
    serverName: String,
    serverUrl: String,
    onServerNameChange: (String) -> Unit,
    onServerUrlChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("编辑服务器") },
            text = {
                Column {
                    OutlinedTextField(
                        value = serverName,
                        onValueChange = onServerNameChange,
                        label = { Text("服务器名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = onServerUrlChange,
                        label = { Text("服务器地址") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = onSave) {
                    Text("保存")
                }
            },
            dismissButton = {
                Button(onClick = onCancel) {
                    Text("取消")
                }
            }
        )
    }
}