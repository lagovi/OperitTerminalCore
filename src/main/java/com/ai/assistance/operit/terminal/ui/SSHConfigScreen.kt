package com.ai.assistance.operit.terminal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.terminal.R
import com.ai.assistance.operit.terminal.data.SSHAuthType
import com.ai.assistance.operit.terminal.data.SSHConfig

/**
 * SSH 配置界面（单一配置）
 */
@Composable
fun SSHConfigScreen(
    config: SSHConfig?,
    onSave: (SSHConfig) -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.terminal_ssh_config_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SettingsTheme.onSurfaceColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (config == null) {
            // 无配置，显示添加按钮
            Text(
                text = stringResource(R.string.terminal_ssh_config_empty),
                color = SettingsTheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            var showDialog by remember { mutableStateOf(false) }
            
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SettingsTheme.primaryColor
                )
            ) {
                Text(stringResource(R.string.terminal_ssh_config_set_action))
            }
            
            if (showDialog) {
                SSHConfigEditDialog(
                    config = null,
                    onDismiss = { showDialog = false },
                    onConfirm = { newConfig ->
                        onSave(newConfig)
                        showDialog = false
                    }
                )
            }
        } else {
            // 显示当前配置
            SSHConfigCard(
                config = config,
                onEdit = { newConfig -> onSave(newConfig) },
                onDelete = onDelete
            )
        }
    }
}

/**
 * SSH 配置卡片
 */
@Composable
private fun SSHConfigCard(
    config: SSHConfig,
    onEdit: (SSHConfig) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SettingsTheme.surfaceColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 配置信息
            Text(
                text = "${config.username}@${config.host}:${config.port}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SettingsTheme.onSurfaceColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.terminal_ssh_config_auth_type,
                    stringResource(
                        if (config.authType == SSHAuthType.PASSWORD) R.string.terminal_ssh_auth_type_password
                        else R.string.terminal_ssh_auth_type_public_key
                    )
                ),
                fontSize = 14.sp,
                color = SettingsTheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 使用提示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SettingsTheme.primaryColor.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.terminal_ssh_config_exit_hint),
                        fontSize = 12.sp,
                        color = SettingsTheme.onSurfaceColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 操作按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SettingsTheme.primaryColor
                    )
                ) {
                    Icon(Icons.Default.Edit, stringResource(R.string.terminal_edit), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.terminal_edit))
                }
                
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SettingsTheme.errorColor
                    )
                ) {
                    Icon(Icons.Default.Delete, stringResource(R.string.delete), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
    
    // 编辑对话框
    if (showEditDialog) {
        SSHConfigEditDialog(
            config = config,
            onDismiss = { showEditDialog = false },
            onConfirm = { newConfig ->
                onEdit(newConfig)
                showEditDialog = false
            }
        )
    }
    
    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    stringResource(R.string.terminal_ssh_config_delete_title),
                    color = SettingsTheme.onSurfaceColor
                )
            },
            text = {
                Text(
                    stringResource(R.string.terminal_ssh_config_delete_message),
                    color = SettingsTheme.onSurfaceColor
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsTheme.errorColor
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), color = SettingsTheme.primaryColor)
                }
            },
            containerColor = SettingsTheme.surfaceColor
        )
    }
}

/**
 * SSH 配置编辑对话框
 */
@Composable
fun SSHConfigEditDialog(
    config: SSHConfig? = null,
    onDismiss: () -> Unit,
    onConfirm: (SSHConfig) -> Unit
) {
    var host by remember { mutableStateOf(config?.host ?: "") }
    var port by remember { mutableStateOf(config?.port?.toString() ?: "22") }
    var username by remember { mutableStateOf(config?.username ?: "") }
    var authType by remember { mutableStateOf(config?.authType ?: SSHAuthType.PASSWORD) }
    var password by remember { mutableStateOf(config?.password ?: "") }
    var privateKeyPath by remember { mutableStateOf(config?.privateKeyPath ?: "") }
    var passphrase by remember { mutableStateOf(config?.passphrase ?: "") }
    
    // 反向隧道配置
    var enableReverseTunnel by remember { mutableStateOf(config?.enableReverseTunnel ?: false) }
    var remoteTunnelPort by remember { mutableStateOf(config?.remoteTunnelPort?.toString() ?: "8881") }
    var localSshPort by remember { mutableStateOf(config?.localSshPort?.toString() ?: "2223") }
    var localSshUsername by remember { mutableStateOf(config?.localSshUsername ?: "android") }
    var localSshPassword by remember { mutableStateOf(config?.localSshPassword ?: "3688368398") }
    
    // 心跳包配置
    var enableKeepAlive by remember { mutableStateOf(config?.enableKeepAlive ?: true) }
    var keepAliveInterval by remember { mutableStateOf(config?.keepAliveInterval?.toString() ?: "30") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (config == null) {
                    stringResource(R.string.terminal_ssh_config_add_title)
                } else {
                    stringResource(R.string.terminal_ssh_config_edit_title)
                },
                color = SettingsTheme.onSurfaceColor
            )
        },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text(stringResource(R.string.terminal_ssh_field_host)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SettingsTheme.onSurfaceColor,
                            unfocusedTextColor = SettingsTheme.onSurfaceColor,
                            focusedBorderColor = SettingsTheme.primaryColor,
                            unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                            focusedLabelColor = SettingsTheme.primaryColor,
                            unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                        )
                    )
                }
                
                item { Spacer(Modifier.height(8.dp)) }
                
                item {
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text(stringResource(R.string.terminal_ssh_field_port)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SettingsTheme.onSurfaceColor,
                            unfocusedTextColor = SettingsTheme.onSurfaceColor,
                            focusedBorderColor = SettingsTheme.primaryColor,
                            unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                            focusedLabelColor = SettingsTheme.primaryColor,
                            unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                        )
                    )
                }
                
                item { Spacer(Modifier.height(8.dp)) }
                
                item {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(R.string.terminal_ssh_field_username)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SettingsTheme.onSurfaceColor,
                            unfocusedTextColor = SettingsTheme.onSurfaceColor,
                            focusedBorderColor = SettingsTheme.primaryColor,
                            unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                            focusedLabelColor = SettingsTheme.primaryColor,
                            unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                        )
                    )
                }
                
                item { Spacer(Modifier.height(8.dp)) }
                
                // 认证方式选择
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = authType == SSHAuthType.PASSWORD,
                            onClick = { authType = SSHAuthType.PASSWORD },
                            label = { Text(stringResource(R.string.terminal_ssh_auth_method_password)) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = authType == SSHAuthType.PUBLIC_KEY,
                            onClick = { authType = SSHAuthType.PUBLIC_KEY },
                            label = { Text(stringResource(R.string.terminal_ssh_auth_method_public_key)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item { Spacer(Modifier.height(8.dp)) }
                
                // 根据认证类型显示不同字段
                if (authType == SSHAuthType.PASSWORD) {
                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_password)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                } else {
                    item {
                        OutlinedTextField(
                            value = privateKeyPath,
                            onValueChange = { privateKeyPath = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_private_key)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                    
                    item { Spacer(Modifier.height(8.dp)) }
                    
                    item {
                        OutlinedTextField(
                            value = passphrase,
                            onValueChange = { passphrase = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_passphrase)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                }
                
                // 心跳包配置分隔符
                item { Spacer(Modifier.height(16.dp)) }
                
                item {
                    HorizontalDivider(
                        color = SettingsTheme.onSurfaceColor.copy(alpha = 0.2f)
                    )
                }
                
                item { Spacer(Modifier.height(8.dp)) }
                
                // 心跳包开关
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.terminal_ssh_keepalive_enable),
                                color = SettingsTheme.onSurfaceColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.terminal_ssh_keepalive_desc),
                                color = SettingsTheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = enableKeepAlive,
                            onCheckedChange = { enableKeepAlive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SettingsTheme.primaryColor,
                                checkedTrackColor = SettingsTheme.primaryColor.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                
                // 如果启用心跳包，显示配置字段
                if (enableKeepAlive) {
                    item { Spacer(Modifier.height(12.dp)) }
                    
                    item {
                        OutlinedTextField(
                            value = keepAliveInterval,
                            onValueChange = { keepAliveInterval = it },
                            label = { Text(stringResource(R.string.terminal_ssh_keepalive_interval)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                    
                    item { Spacer(Modifier.height(4.dp)) }
                    
                    item {
                        Text(
                            text = stringResource(R.string.terminal_ssh_keepalive_tip),
                            color = SettingsTheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                
                // 反向隧道配置分隔符
                item { Spacer(Modifier.height(16.dp)) }
                
                item {
                    HorizontalDivider(
                        color = SettingsTheme.onSurfaceColor.copy(alpha = 0.2f)
                    )
                }
                
                item { Spacer(Modifier.height(8.dp)) }
                
                // 反向挂载开关
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.terminal_ssh_reverse_enable),
                                color = SettingsTheme.onSurfaceColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.terminal_ssh_reverse_desc),
                                color = SettingsTheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = enableReverseTunnel,
                            onCheckedChange = { enableReverseTunnel = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SettingsTheme.primaryColor,
                                checkedTrackColor = SettingsTheme.primaryColor.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
                
                // 反向挂载说明
                item { Spacer(Modifier.height(8.dp)) }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SettingsTheme.primaryColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = stringResource(R.string.terminal_ssh_reverse_info_title),
                                color = SettingsTheme.primaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.terminal_ssh_reverse_req_local),
                                color = SettingsTheme.onSurfaceColor,
                                fontSize = 12.sp
                            )
                            Text(
                                text = stringResource(R.string.terminal_ssh_reverse_req_remote),
                                color = SettingsTheme.onSurfaceColor,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.terminal_ssh_reverse_access_note),
                                color = SettingsTheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                
                // 如果启用反向隧道，显示配置字段
                if (enableReverseTunnel) {
                    item { Spacer(Modifier.height(12.dp)) }
                    
                    item {
                        OutlinedTextField(
                            value = remoteTunnelPort,
                            onValueChange = { remoteTunnelPort = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_remote_tunnel_port)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                    
                    item { Spacer(Modifier.height(8.dp)) }
                    
                    item {
                        OutlinedTextField(
                            value = localSshPort,
                            onValueChange = { localSshPort = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_local_port)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                    
                    item { Spacer(Modifier.height(8.dp)) }
                    
                    item {
                        OutlinedTextField(
                            value = localSshUsername,
                            onValueChange = { localSshUsername = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_local_username)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                    
                    item { Spacer(Modifier.height(8.dp)) }
                    
                    item {
                        OutlinedTextField(
                            value = localSshPassword,
                            onValueChange = { localSshPassword = it },
                            label = { Text(stringResource(R.string.terminal_ssh_field_local_password)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = SettingsTheme.onSurfaceColor,
                                unfocusedTextColor = SettingsTheme.onSurfaceColor,
                                focusedBorderColor = SettingsTheme.primaryColor,
                                unfocusedBorderColor = SettingsTheme.onSurfaceColor.copy(alpha = 0.5f),
                                focusedLabelColor = SettingsTheme.primaryColor,
                                unfocusedLabelColor = SettingsTheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newConfig = SSHConfig(
                        host = host,
                        port = port.toIntOrNull() ?: 22,
                        username = username,
                        authType = authType,
                        password = if (authType == SSHAuthType.PASSWORD) password else null,
                        privateKeyPath = if (authType == SSHAuthType.PUBLIC_KEY) privateKeyPath else null,
                        passphrase = if (authType == SSHAuthType.PUBLIC_KEY && passphrase.isNotEmpty()) passphrase else null,
                        // 反向隧道配置
                        enableReverseTunnel = enableReverseTunnel,
                        remoteTunnelPort = remoteTunnelPort.toIntOrNull() ?: 8881,
                        localSshPort = localSshPort.toIntOrNull() ?: 2223,
                        localSshUsername = localSshUsername,
                        localSshPassword = localSshPassword,
                        // 心跳包配置
                        enableKeepAlive = enableKeepAlive,
                        keepAliveInterval = keepAliveInterval.toIntOrNull() ?: 30
                    )
                    onConfirm(newConfig)
                },
                enabled = host.isNotBlank() && username.isNotBlank() &&
                        (authType == SSHAuthType.PUBLIC_KEY && privateKeyPath.isNotBlank() ||
                         authType == SSHAuthType.PASSWORD && password.isNotBlank()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SettingsTheme.primaryColor
                )
            ) {
                Text(stringResource(R.string.terminal_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = SettingsTheme.primaryColor)
            }
        },
        containerColor = SettingsTheme.surfaceColor
    )
}
