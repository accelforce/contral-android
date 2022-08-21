package net.accelf.contral.mastodon.pages.accounts.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.accelf.contral.api.ui.theme.ContralTheme
import net.accelf.contral.api.ui.utils.useState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ConfigureDomain(
    onDomainSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var domain by useState(value = "")
    Column(
        modifier = modifier,
    ) {
        Text(text = "Type the domain of your instance")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = domain,
                onValueChange = {
                    domain = it
                },
                modifier = Modifier.weight(1f),
                label = { Text(text = "Domain") },
            )

            Button(
                onClick = {
                    onDomainSubmit(domain)
                },
                modifier = Modifier
                    .requiredWidth(IntrinsicSize.Min)
                    .padding(start = 4.dp),
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Composable
@Preview
private fun PreviewConfigureDomain() {
    ContralTheme {
        ConfigureDomain(
            onDomainSubmit = {},
        )
    }
}
