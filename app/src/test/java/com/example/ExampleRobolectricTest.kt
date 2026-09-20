package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.GameUiState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Çarpma Alıştırması", appName)
  }

  @Test
  fun `star calculation meets user specification`() {
    // 0 to 640 -> 1 star
    assertEquals(1, GameUiState.calculateStars(0))
    assertEquals(1, GameUiState.calculateStars(350))
    assertEquals(1, GameUiState.calculateStars(639))

    // 640 to 1500 -> 2 stars
    assertEquals(2, GameUiState.calculateStars(640))
    assertEquals(2, GameUiState.calculateStars(1000))
    assertEquals(2, GameUiState.calculateStars(1499))

    // 1500+ -> 3 stars
    assertEquals(3, GameUiState.calculateStars(1500))
    assertEquals(3, GameUiState.calculateStars(2500))
  }
}

