package com.aegisdev.ktreesitter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aegisdev.ktreesitter.editor.EditorView
import com.aegisdev.ktreesitter.editor.languages.LanguageManager

@Composable
fun App() {
    // Create the LanguageManager once and remember it for the lifetime of the App.
    // This ensures our language configurations are loaded only once.
    val languageManager = remember { LanguageManager() }

    val javaText = """
    public class HelloWorld {
        public static void main(String[] args) {
            // This is a comment
            System.out.println("Hello, Java!");
        }
    }
    """.trimIndent()

    val pythonText = """
    def greet(name):
        # This is a Python comment
        print(f"Hello, {name}!")

    greet("Python")
    """.trimIndent()

    Column(modifier = Modifier.padding(8.dp)) {
        Text("KTreeSitter in Modified EditorKit", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Java Editor Instance
            Column(modifier = Modifier.weight(1f)) {
                Text("Java", style = MaterialTheme.typography.titleMedium)
                EditorView(
                    modifier = Modifier.fillMaxSize(),
                    languageManager = languageManager,
                    languageName = "java",
                    text = javaText
                )
            }

            // Python Editor Instance
            Column(modifier = Modifier.weight(1f)) {
                Text("Python", style = MaterialTheme.typography.titleMedium)
                EditorView(
                    modifier = Modifier.fillMaxSize(),
                    languageManager = languageManager,
                    languageName = "python",
                    text = pythonText
                )
            }
        }
    }
}