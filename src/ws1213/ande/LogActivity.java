/**
 * 
 */
package ws1213.ande;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Sascha Hayton
 * 
 */
public class LogActivity extends AppActivity
{

	@Override
	public void onCreate(Bundle savedInstaceState)
	{
		super.onCreate(savedInstaceState);
		setContentView(R.layout.activity_log);
		TableLayout logTable = (TableLayout) findViewById(R.id.TableView_Log);
		initHeaderRow(logTable);

	}

	private void initHeaderRow(TableLayout logTable)
	{
		TableRow logHeader = new TableRow(this);
		int textColor = getResources().getColor(R.color.normal_blue);
		float textSize = getResources().getDimension(R.dimen.label_size);

		addTextToRowWithValues(logHeader, getResources().getString(R.string.log_date), textColor, textSize);
		addTextToRowWithValues(logHeader, getResources().getString(R.string.log_cost_per_litre), textColor, textSize);
		addTextToRowWithValues(logHeader, getResources().getString(R.string.log_litre), textColor, textSize);
		addTextToRowWithValues(logHeader, getResources().getString(R.string.log_km), textColor, textSize);
		addTextToRowWithValues(logHeader, getResources().getString(R.string.log_location), textColor, textSize);
	}

	private void addTextToRowWithValues(final TableRow tableRow, String text, int textColor, float textSize)
	{
		TextView textView = new TextView(this);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setText(text);
		tableRow.addView(textView);
	}
}
