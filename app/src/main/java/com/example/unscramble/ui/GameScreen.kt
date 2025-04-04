package com.example.unscramble.ui

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramble.R
import com.example.unscramble.ui.theme.UnscrambleTheme

@Composable
fun GameScreenApp() {
    Scaffold(
        topBar = { GameTopAppBar() },
    ) { innerPadding ->
        GameScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier, gameViewModel: GameViewModel = viewModel()) {
    val gameUiState = gameViewModel.uiState

    Column(
        modifier = modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gameUiState.isGameOver) {
            FinalScoreDialog(score = gameUiState.score, onPlayAgain = { gameViewModel.resetGame() })
        }

        GameLayout(
            currentScrambledWord = gameUiState.currentScrambledWord,
            onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
            onKeyboardDone = { gameViewModel.checkUserGuess() },
            userGuess = gameViewModel.userGuess,
            isGuessWrong = gameUiState.isGuessedWordWrong,
            wordCount = gameUiState.currentWordCount,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { gameViewModel.checkUserGuess() }) {
                Text(
                    text = stringResource(R.string.submit),
                    fontSize = 16.sp
                )
            }

            OutlinedButton(
                onClick = { gameViewModel.skipWord() },
                modifier = Modifier.fillMaxWidth()
            ) { Text(text = stringResource(R.string.skip), fontSize = 16.sp) }
        }

        GameStatus(score = gameUiState.score)
    }
}

@Composable
fun GameStatus(score: Int) {
    Card(modifier = Modifier.padding(20.dp)) {
        Text(
            text = stringResource(R.string.score, score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun GameLayout(
    modifier: Modifier = Modifier,
    currentScrambledWord: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    userGuess: String,
    isGuessWrong: Boolean,
    wordCount: Int,
) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                modifier = Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
                text = stringResource(R.string.word_count, wordCount),
                style = typography.titleMedium,
                color = colorScheme.onPrimary
            )
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium,
                fontSize = 45.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)

            )
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = onUserGuessChanged,
                label = {
                    when (isGuessWrong) {
                        true -> Text(stringResource(R.string.wrong_guess))
                        false -> Text(stringResource(R.string.enter_your_word))
                    }
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onKeyboardDone() })
            )
        }
    }
}

@Composable
private fun FinalScoreDialog(score: Int, onPlayAgain: () -> Unit, modifier: Modifier = Modifier) {
    val activity = LocalActivity.current as Activity

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = { TextButton(onClick = { activity.finish() }) { Text(text = stringResource(R.string.exit)) } },
        confirmButton = { TextButton(onClick = onPlayAgain) { Text(text = stringResource(R.string.play_again)) } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopAppBar() {
    CenterAlignedTopAppBar(title = {
        Text(
            text = stringResource(R.string.app_name), style = typography.titleLarge
        )
    })
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenDarkPreview() {
    UnscrambleTheme(darkTheme = true) {
        GameScreenApp()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenLightPreview() {
    UnscrambleTheme(darkTheme = false) {
        GameScreenApp()
    }
}