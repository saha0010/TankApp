/**
 * 
 */
package ws1213.ande;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Sascha Hayton
 *
 */

public class StatsActivity extends AppActivity {
	
    float bspVal1 = 8.7f;
    int bspVal2 = 435;
    float bspVal3 = 1.61f;
    float bspVal4 = 60.93f;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        
        TableLayout statsTable = (TableLayout)findViewById(R.id.TableLayout_stats);
        
        TableRow statsTableRow1 =(TableRow)findViewById(R.id.tableRow_stats_FirstRow);
        TableRow statsTableRow2 =(TableRow)findViewById(R.id.tableRow_stats_SecondRow);
        TableRow statsTableRow3 =(TableRow)findViewById(R.id.tableRow_stats_ThirdRow);
        TableRow statsTableRow4 =(TableRow)findViewById(R.id.tableRow_stats_ForthRow);
        
        String row1Currency = getResources().getString(R.string.stats_litre);
        String row2Currency = getResources().getString(R.string.stats_length_notaion);
        String row3Currency = getResources().getString(R.string.stats_currency);
        String row4Currency = row3Currency;
        
        addTextToRowWithValues(statsTableRow1, bspVal1+"", row1Currency);
        addTextToRowWithValues(statsTableRow2, bspVal2+"", row2Currency);
        addTextToRowWithValues(statsTableRow3, bspVal3+"", row3Currency);
        addTextToRowWithValues(statsTableRow4, bspVal4+"", row4Currency);
               
	}
    private void addTextToRowWithValues(final TableRow tableRow, String value, String currency) {
    	
    	int textColor = getResources().getColor(R.color.normal_green);
    	float textSize = getResources().getDimension(R.dimen.label_size);
    	
        TextView textView = new TextView(this);
     
        textView.setText(value+" "+currency);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.RIGHT);
        tableRow.addView(textView);
    }
}
