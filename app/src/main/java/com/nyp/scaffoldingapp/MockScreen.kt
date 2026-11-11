package practice.wk03

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Week 3 practice — build the screen in materials/wk03/mockup-brief.md
 *
 *  ┌────────────────────────────────────────────┐
 *  │  ( ◉ )  Weekly Planner              ♡      │  1. header
 *  │         14 tasks this week                 │
 *  │                                            │
 *  │   ┌────────┬────────┬────────┐             │  2. stats row
 *  │   │   8    │   4    │   2    │             │
 *  │   │  done  │ active │overdue │             │
 *  │   └────────┴────────┴────────┘             │
 *  │                                            │
 *  │  Today                                     │  3. heading
 *  │  ────────────────────────────────────────  │
 *  │  ●  Submit A1 draft            4 Dec        │  4. three items,
 *  │  ●  Read the rubric            2 Dec        │     same layout
 *  │  ●  Set up the emulator        1 Dec        │
 *  │                                            │
 *  │  [  Add task  ]   [  View all  ]           │  5. two buttons
 *  └────────────────────────────────────────────┘
 *
 * Do the TODOs in order. Preview after each one.
 */
@Composable
fun MockScreen(modifier: Modifier = Modifier) {

    // TODO 6: the heart needs to remember whether it is filled.
    //         Declare that state here. (One line. We come back to it.)

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---- 1. Header -------------------------------------------------
        // A Row containing: a circle, then a Column of title + subtitle,
        // then the heart pushed to the far right.
        //
        // TODO 1a: make the Box below circular and give it a colour.
        //          Hint: Modifier.size(...).clip(CircleShape).background(...)
        // TODO 1b: put the title and subtitle in a Column beside it.
        //          Use MaterialTheme.typography — no hardcoded sp.
        // TODO 1c: push the heart to the right edge.
        //          Hint: Modifier.weight(1f) on the part that should stretch.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(100.dp, 100.dp).clip(CircleShape).background(color = Color.Blue))                     // ← the avatar
            Spacer(Modifier.width(12.dp))

            // title + subtitle go here
            val typography = MaterialTheme.typography.headlineSmall
            Column(modifier = Modifier.weight(1f))
            {
                Text("Weekly Planner", style = typography)
                Text("14 Tasks this week", style = typography)
            }
            Text("♡", Modifier.clickable{ /* TODO 6b */ })
        }

        Spacer(Modifier.height(24.dp))

        // ---- 2. Stats row ----------------------------------------------
        // Three stats sharing the width evenly.
        //
        // TODO 2: give each Stat a Modifier.weight(1f) so they share the row.
        //         Try it without weight first, and look at what happens.
        Row(Modifier.fillMaxWidth()) {
            Stat(value = "8", label = "done", Modifier.weight(0.5f))
            Stat(value = "4", label = "active", Modifier.weight(0.5f))
            Stat(value = "2", label = "overdue", Modifier.weight(0.5f))
        }

        Spacer(Modifier.height(24.dp))

        // ---- 3. Section heading ----------------------------------------
        // TODO 3: a heading, then a divider under it.
        //         Hint: HorizontalDivider()
        Text("Today")
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = Color.Black)

        // ---- 4. Three items --------------------------------------------
        // TODO 4: three TaskRow calls with different content.
        //         Write them out by hand. Yes, all three. You will find out
        //         in Week 4 why that stops being a good idea.
        Spacer(Modifier.height(10.dp))
        Text("Submit A1 draft")
        Spacer(Modifier.height(10.dp))
        Text("Read the rubric")
        Spacer(Modifier.height(10.dp))
        Text("Set up emulator")
        Spacer(Modifier.weight(1f))     // pushes the buttons to the bottom

        // ---- 5. Two buttons --------------------------------------------
        // TODO 5: two buttons side by side, sharing the width.

    }
}

/** One statistic: a big number over a small label. */
@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

/** One row in the list: a dot, a title that flexes, and a date. */
@Composable
private fun TaskRow(title: String, date: String, modifier: Modifier = Modifier) {
    // TODO 4b: a Row with a small circular dot, the title, and the date
    //          on the far right. Same weight(1f) trick as the header.
}

// ---- 6. Previews ---------------------------------------------------------
// TODO: a second preview showing a DIFFERENT state — try the empty version,
//       or a very long title. Two previews of the same composable is normal.
@Preview(showBackground = true)
@Composable
private fun MockScreenPreview() {
    MaterialTheme { MockScreen() }
}
