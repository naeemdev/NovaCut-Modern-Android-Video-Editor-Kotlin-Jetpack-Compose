package com.novacut.editor.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novacut.editor.BrandConfig
import com.novacut.editor.domain.model.Project
import com.novacut.editor.presentation.ui.profile.ProfileScreen
import com.novacut.editor.presentation.ui.templates.TemplatesScreen
import com.novacut.editor.presentation.viewmodel.ProjectsViewModel
import com.novacut.editor.theme.NovaCutTheme
import com.novacut.editor.theme.StudioBorder
import com.novacut.editor.theme.StudioDarkBg
import com.novacut.editor.theme.StudioPrimary
import com.novacut.editor.theme.StudioSecondary
import com.novacut.editor.theme.StudioSurface
import com.novacut.editor.theme.StudioSurfaceElevated
import com.novacut.editor.theme.StudioTextPrimary
import com.novacut.editor.theme.StudioTextSecondary

@Composable
fun HomeScreen(
    viewModel: ProjectsViewModel,
    onNavigateToEditor: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToAutoCut: () -> Unit,
    onNavigateToCaptions: () -> Unit,
    onNavigateToAudioExtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.uiState.collectAsState()
    val isCreating by viewModel.isCreating.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
    ) {
        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> EditHomeContent(
                    projects = projects,
                    isCreating = isCreating,
                    onCreateProject = {
                        viewModel.createNewProject { newId ->
                            onNavigateToEditor(newId)
                        }
                    },
                    onNavigateToEditor = onNavigateToEditor,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToCamera = onNavigateToCamera,
                    onNavigateToAutoCut = onNavigateToAutoCut,
                    onNavigateToCaptions = onNavigateToCaptions,
                    onNavigateToAudioExtract = onNavigateToAudioExtract,
                    onDuplicate = { viewModel.duplicateProject(it) },
                    onDelete = { viewModel.deleteProject(it) }
                )
                1 -> TemplatesScreen(
                    onTemplateClick = { template ->
                        // Automatically create a new project seeded from the viral template!
                        viewModel.createNewProject { newId ->
                            onNavigateToEditor(newId)
                        }
                    }
                )
                2 -> ProfileScreen(
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }

        // Bottom Navigation Bar
        NavigationBar(
            containerColor = StudioSurface,
            contentColor = StudioTextPrimary,
            tonalElevation = 8.dp,
            modifier = Modifier.navigationBarsPadding()
        ) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Edit"
                    )
                },
                label = { Text("Edit", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StudioPrimary,
                    selectedTextColor = StudioPrimary,
                    unselectedIconColor = StudioTextSecondary,
                    unselectedTextColor = StudioTextSecondary,
                    indicatorColor = StudioSurfaceElevated
                )
            )

            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Templates"
                    )
                },
                label = { Text("Templates", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StudioPrimary,
                    selectedTextColor = StudioPrimary,
                    unselectedIconColor = StudioTextSecondary,
                    unselectedTextColor = StudioTextSecondary,
                    indicatorColor = StudioSurfaceElevated
                )
            )

            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Me"
                    )
                },
                label = { Text("Me", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StudioPrimary,
                    selectedTextColor = StudioPrimary,
                    unselectedIconColor = StudioTextSecondary,
                    unselectedTextColor = StudioTextSecondary,
                    indicatorColor = StudioSurfaceElevated
                )
            )
        }
    }
}

@Composable
private fun EditHomeContent(
    projects: List<Project>,
    isCreating: Boolean,
    onCreateProject: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToAutoCut: () -> Unit,
    onNavigateToCaptions: () -> Unit,
    onNavigateToAudioExtract: () -> Unit,
    onDuplicate: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val quickTools = remember(onNavigateToAutoCut, onNavigateToCamera, onNavigateToCaptions, onNavigateToAudioExtract) {
        listOf(
            QuickTool("AutoCut", Icons.Default.AutoAwesome, onNavigateToAutoCut),
            QuickTool("Camera", Icons.Default.Videocam, onNavigateToCamera),
            QuickTool("Captions", Icons.Default.Subtitles, onNavigateToCaptions),
            QuickTool("Audio Extract", Icons.Default.Audiotrack, onNavigateToAudioExtract),
            QuickTool("Retouch", Icons.Default.ContentCut, onNavigateToCamera)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(StudioPrimary, StudioSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = StudioDarkBg,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = BrandConfig.APP_NAME,
                            color = StudioTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(StudioPrimary, StudioSecondary)
                                    )
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Text(
                        text = BrandConfig.APP_SUBTITLE,
                        color = StudioTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(StudioSurface)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Hero "New Project" Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(StudioSurfaceElevated, StudioSurface)
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(StudioPrimary.copy(alpha = 0.6f), StudioSecondary.copy(alpha = 0.6f))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(enabled = !isCreating, onClick = onCreateProject)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(
                            color = StudioPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(StudioPrimary, StudioSecondary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Project",
                                    tint = StudioDarkBg,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "New Project",
                                color = StudioTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start editing your video clips with pro tools",
                                color = StudioTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Quick Studio Tools Row
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quickTools) { tool ->
                        QuickToolItem(tool = tool)
                    }
                }
            }

            // Recent Projects Header
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Recent Projects",
                            color = StudioTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(StudioSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${projects.size}",
                                color = StudioPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Projects List
            if (projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = StudioBorder,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No drafts yet",
                                color = StudioTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(projects, key = { it.id }) { project ->
                    ProjectCard(
                        project = project,
                        onClick = { onNavigateToEditor(project.id) },
                        onDuplicate = { onDuplicate(project.id) },
                        onDelete = { onDelete(project.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

private data class QuickTool(val title: String, val icon: ImageVector, val action: () -> Unit)

@Composable
private fun QuickToolItem(tool: QuickTool) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(StudioSurface)
            .clickable(onClick = tool.action)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(StudioSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                tint = StudioPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tool.title,
            color = StudioTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(name = "Home Screen with Bottom Navigation", showBackground = true)
@Composable
fun HomeScreenPreview() {
    NovaCutTheme {
        EditHomeContent(
            projects = emptyList(),
            isCreating = false,
            onCreateProject = {},
            onNavigateToEditor = {},
            onNavigateToSettings = {},
            onNavigateToCamera = {},
            onNavigateToAutoCut = {},
            onNavigateToCaptions = {},
            onNavigateToAudioExtract = {},
            onDuplicate = {},
            onDelete = {}
        )
    }
}
