package com.approdo.stockquote;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public final static String STOCK_SYMBOL = "com.example.stockqoute.STOCK";
	
	private SharedPreferences stockSymbolsEntered;
	
	private TableLayout stockTableScrollView;
	
	private EditText stockSymbolEditText;
	
	Button enterStockSymbolButton;
	Button deleteStocksButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);
		
		stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
		
		stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
		
		enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);
		deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);
		
		enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
		deleteStocksButton.setOnClickListener(deleteStocksButtonListener);
		
		updateSavedStockList(null);
	}
	
	private void updateSavedStockList(String newStockSymbol){
		
		String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
		Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);
		
		if(newStockSymbol != null){
			insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
		} else{
			for(int i = 0; i < stocks.length; i++){
				insertStockInScrollView(stocks[1], i);
			}
		}
	}
	
	@SuppressLint("NewApi")
	private void saveStockSymbol(String newStock){
		String isTheStockNew = stockSymbolsEntered.getString(newStock, null);
		SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
		
		preferencesEditor.putString(newStock, newStock);
		preferencesEditor.apply();
		
		if(isTheStockNew == null){
			updateSavedStockList(newStock);
		}
	}
	
	private void insertStockInScrollView(String stock, int arrayIndex){
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newStockRow = inflater.inflate(R.layout.stock_qoute_row, null);
		
		TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);
		
		newStockTextView.setText(stock);
		
		Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);
		stockQuoteButton.setOnClickListener(getStockActivityListener);
		Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);
		stockQuoteButton.setOnClickListener(getStockFromWebsiteListener);
		
		stockTableScrollView.addView(newStockRow, arrayIndex);
	}
	
	public OnClickListener enterStockButtonListener = new OnClickListener(){

	    @Override
	    public void onClick(View theView) {
	        // If there is a stock symbol entered into the EditText
	        // field
	        if(stockSymbolEditText.getText().length() > 0){	             
	            // Save the new stock and add its components
	            saveStockSymbol(stockSymbolEditText.getText().toString());
	             
	            stockSymbolEditText.setText(""); // Clear EditText box
	             
	            // Force the keyboard to close
	            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
	        } else {	             
	            // Create an alert dialog box
	            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	             
	            // Set alert title
	            builder.setTitle(R.string.invalid_stock_symbol);
	             
	            // Set the value for the positive reaction from the user
	            // You can also set a listener to call when it is pressed
	            builder.setPositiveButton(R.string.ok, null);
	             
	            // The message
	            builder.setMessage(R.string.missing_stock_symbol);
	             
	            // Create the alert dialog and display it
	            AlertDialog theAlertDialog = builder.create();
	            theAlertDialog.show();	             
	        }	         
	    }	     
	};
	
	private void deleteAllStocks(){
		stockTableScrollView.removeAllViews();
	}
	
	@SuppressLint("NewApi")
	public OnClickListener deleteStocksButtonListener = new OnClickListener(){

	    public void onClick(View v) {	         
	        deleteAllStocks();
	         
	        // Editor is used to store a key / value pairs	         
	        SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
	         
	        // Here I'm deleting the key / value pairs
	        preferencesEditor.clear();
	        preferencesEditor.apply();         
	    }  
	};
	
	public OnClickListener getStockActivityListener = new OnClickListener(){

	    public void onClick(View v) {
	         
	        // Get the text saved in the TextView next to the clicked button
	        // with the id stockSymbolTextView
	        TableRow tableRow = (TableRow) v.getParent();
	        TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);
	        String stockSymbol = stockTextView.getText().toString();
	         
	        // An intent is an object that can be used to start another activity
	        Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
	         
	        // Add the stock symbol to the intent
	        intent.putExtra(STOCK_SYMBOL, stockSymbol);
	         
	        startActivity(intent);         
	    }	     
	};
	
	public OnClickListener getStockFromWebsiteListener = new OnClickListener(){

	    public void onClick(View v) {	         
	        // Get the text saved in the TextView next to the clicked button
	        // with the id stockSymbolTextView

	        TableRow tableRow = (TableRow) v.getParent();
	        TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);
	        String stockSymbol = stockTextView.getText().toString();
	         
	        // The URL specific for the stock symbol
	        String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;
	         
	        Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
	         
	        startActivity(getStockWebPage);	         
	    }
	     
	};
}
