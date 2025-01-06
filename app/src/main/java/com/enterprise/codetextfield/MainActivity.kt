package com.enterprise.codetextfield

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.enterprise.codetextfield.ui.theme.AppGreen
import com.enterprise.codetextfield.ui.theme.AppLightGray
import com.enterprise.codetextfield.ui.theme.CodeTextFieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeTextFieldTheme {
                CodeTextFieldApp()
            }
        }
    }
}

@Composable
fun CodeTextFieldApp() {

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(color = Color.Green)){

        Scaffold(modifier = Modifier.systemBarsPadding().fillMaxSize()) { innerPadding ->

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(innerPadding).fillMaxSize()
                    .background(color = Color.White)){

                val textInputNumber = remember { mutableStateOf("") }
                Text(text = stringResource(id = R.string.main_activity_code_textfield_number))
                Text(textInputNumber.value)

                CodeTextFieldNumber(text = textInputNumber, numberOfDigits = 4)


                val textInputCharacter = remember { mutableStateOf("") }
                Text(text = stringResource(id = R.string.main_activity_code_textfield_character))
                Text(textInputCharacter.value)

                CodeTextFieldCharacter(text = textInputCharacter, numberOfCharacters = 5)

            }

        }

    }

}

@Composable
fun CodeTextFieldNumber(text: MutableState<String>, numberOfDigits: Int){

    val focusRequesters = remember {
        List(numberOfDigits) { FocusRequester() }
    }

    val digits = remember {
        List<MutableState<String?>>(numberOfDigits) { mutableStateOf<String?>(null) }
    }

    var output = ""
    for (digit in digits){

        output = output + digit.value.toString()

    }
    text.value = output

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth().padding(10.dp)){

        val firstIndex = 0
        val lastIndex = numberOfDigits - 1

        (firstIndex..lastIndex).forEachIndexed { index, element ->

            InputFieldNumber(digits[index].value?.toIntOrNull(), focusRequester = focusRequesters[index],
                onFocusChanged = {},
                onNumberChanged = { number -> digits[index].value = number.toString()
                                     if(number!=null) {
                                       focusRequesters[(index + 1).coerceIn(firstIndex,lastIndex)]
                                           .requestFocus()
                                     }
                                  },
                onKeyboardBack = {focusRequesters[(index - 1).coerceIn(firstIndex, lastIndex)].requestFocus()},
                modifier = Modifier.weight(1F).aspectRatio(1F))

        }
    }

}



@Composable
fun InputFieldNumber(
    number: Int?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onNumberChanged: (Int?) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val text by remember(number) {
        mutableStateOf(
            TextFieldValue(
                text = number?.toString().orEmpty(),
                selection = TextRange(
                    index = if(number != null) 1 else 0
                )
            )
        )
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = AppGreen
            )
            .background(AppLightGray),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                val newNumber = newText.text
                if(newNumber.length <= 1 && newNumber.isDigitsOnly()) {
                    onNumberChanged(newNumber.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(AppGreen),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 36.sp,
                color = AppGreen
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier
                .padding(10.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event ->
                    val didPressDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
                    if(didPressDelete && number == null) {
                        onKeyboardBack()
                    }
                    false
                },
            decorationBox = { innerBox ->
                innerBox()
                if(!isFocused && number == null) {
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        color = AppGreen,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }
        )
    }
}


@Composable
fun CodeTextFieldCharacter(text: MutableState<String>, numberOfCharacters: Int){

    val focusRequesters = remember {
        List(numberOfCharacters) { FocusRequester() }
    }

    val characters = remember {
        List<MutableState<String?>>(numberOfCharacters) { mutableStateOf<String?>(null) }
    }

    var output = ""
    for (character in characters){

        output = output + character.value.toString()

    }
    text.value = output

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth().padding(10.dp)){

        val firstIndex = 0
        val lastIndex = numberOfCharacters - 1

        (firstIndex..lastIndex).forEachIndexed { index, element ->

            InputFieldCharacter(characters[index].value, focusRequester = focusRequesters[index],
                onFocusChanged = {},
                onNumberChanged = { character -> characters[index].value = character.toString()
                    if(!character.isNullOrEmpty()){
                        focusRequesters[(index +1).coerceIn(firstIndex, lastIndex)].requestFocus()
                    }
                                  } ,
                onKeyboardBack = {focusRequesters[(index - 1).coerceIn(firstIndex, lastIndex)].requestFocus()},
                modifier = Modifier.weight(1F).aspectRatio(1F))

        }
    }

}


@Composable
fun InputFieldCharacter(
    inputCharacter: String?,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onNumberChanged: (String?) -> Unit,
    onKeyboardBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val text by remember(inputCharacter) {
        mutableStateOf(
            TextFieldValue(
                text = inputCharacter.orEmpty(),
                selection = TextRange(
                    index = if(inputCharacter != null) 1 else 0
                )
            )
        )
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = AppGreen
            )
            .background(AppLightGray),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                var newChar = newText.text
                if(newChar.length <= 1) {

                       onNumberChanged(newChar)

                }
            },
            cursorBrush = SolidColor(AppGreen),
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 36.sp,
                color = AppGreen
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier
                .padding(10.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event ->
                    val didPressDelete = event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL
                    if(didPressDelete && inputCharacter.isNullOrEmpty()) {
                        onKeyboardBack()
                    }
                    false
                },
            decorationBox = { innerBox ->
                innerBox()
                if(!isFocused && inputCharacter.isNullOrEmpty()) {
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        color = AppGreen,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
            }
        )
    }
}

