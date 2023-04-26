package com.kseniabl.tasksapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kseniabl.tasksapp.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WelcomePage(
    navigate: () -> Unit
) {
    val pageData = listOf(
        PageData("Welcome!", R.drawable.effective_one_pic, "We are glad to see you in our app! Let's get acquainted!"),
        PageData("Create your own tasks!", R.drawable.effective_two_pic, "Fill information about yourself and add tasks!"),
        PageData("Book other's tasks!", R.drawable.effective_three_pic, "Book and chat with other users")
    )

    Surface(
        contentColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val pageState = rememberPagerState()
            val coroutineScope = rememberCoroutineScope()

            HorizontalPager(
                count = pageData.size,
                state = pageState
            ) { pageIdx ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = pageData[pageIdx].image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pageData[pageIdx].title,
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pageData[pageIdx].content,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (pageData.size-1 != currentPage)
                                    pageState.scrollToPage(currentPage+1)
                                else
                                    navigate()
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .wrapContentSize()
                            .height(50.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        colorResource(id = R.color.blue),
                                        colorResource(id = R.color.purple)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Text(
                            text = if (pageIdx != pageData.size-1) "Next" else "Finish",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}


data class PageData(
    val title: String,
    val image: Int,
    val content: String
)