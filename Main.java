
//URI for the API
	String url = "http://www.apilayer.net/api/live?access_key=***&currencies=AUD,USD,JPY,SGD,EUR,NZD,LKR,MYR&format=1";
	
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	
	//fill arrays
        currVal = getResources().getStringArray(R.array.currencyCode);
        currName = getResources().getStringArray(R.array.currencyName);
        
        //Get the widgets reference from XML layout       
        enteredVal = (EditText) findViewById(R.id.textInputCurrency);
        firstNP = (NumberPicker) findViewById(R.id.firstCurrencyPick);
        secNP = (NumberPicker) findViewById(R.id.secCurrencyPick);
        txtResult = (TextView)findViewById(R.id.txtViewResult);
        txtCurrName = (TextView)findViewById(R.id.firstCurrencyName);
        txtCurrName2 = (TextView)findViewById(R.id.secCurrencyName);
        btnUpdate  = (Button) findViewById(R.id.button_update);
        txtDate = (TextView)findViewById(R.id.labelcurrencyDate);
       
    	// setup the number picker 
        firstNP.setDisplayedValues(currVal);
        firstNP.setMinValue(0);
        firstNP.setMaxValue(7);
        firstNP.setWrapSelectorWheel(true);
        firstNP.setBackgroundColor(Color.parseColor("#6495ED"));
        
        secNP.setDisplayedValues(currVal);
        secNP.setMinValue(0);
        secNP.setMaxValue(7);
        secNP.setWrapSelectorWheel(true);
        secNP.setBackgroundColor(Color.parseColor("#00BFFF"));
	
        setDividerColor(firstNP, Color.parseColor("#800080"));    
        setDividerColor(secNP, Color.parseColor("#4169E1")); 
	
    	//First number picker
       firstNP.setOnValueChangedListener(new OnValueChangeListener() {
    	   @Override
    	   public void onValueChange(NumberPicker picker, int oldVal, int newVal) {   		       		   
    		   txtCurrName.setText(" ");
    		   
    		   //check edit text is empty before pass the value
    		   if(TextUtils.isEmpty(enteredVal.getText().toString())){
    			   MessageBox("Please enter the Currency Value");
    			   return;
    		   }else{
    			   myInput = Double.parseDouble(enteredVal.getText().toString());
    		   }
    		      	switch(newVal){
				case 0 : 
					txtCurrName.setText(currName[0]);
					firstSpinnerVal = audValue;
					myResult = Calculate(myInput, firstSpinnerVal, secSpinnerVal);
					//txtResult.setText(myResult);
					txtResult.setText(Double.toString(myResult));
					
					break;
				case 1 : 
					txtCurrName.setText(currName[1]);
					firstSpinnerVal = usdValue;
					myResult = Calculate(myInput, firstSpinnerVal, secSpinnerVal);
					txtResult.setText(Double.toString(myResult));
					break;
				case 2 : 
					txtCurrName.setText(currName[2]);
					firstSpinnerVal = jpyValue;
					myResult = Calculate(myInput, firstSpinnerVal, secSpinnerVal);
					txtResult.setText(Double.toString(myResult));
					break;
				case 3 : 
				// Continue for rest of the currencies
				
				} // switch	
				myResult = Calculate(myInput, firstSpinnerVal, secSpinnerVal);
				txtResult.setText(Double.toString(myResult));
				np1Val = newVal;
			} //onValueChange
		});//end first NP
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new JsonTask().execute(url);
			}
		});
    }// onCreate
    
    //Function Calculate
    public double Calculate(double input, double valOne, double valTwo)
    {	  	
    	double cal =input * (valTwo/valOne); 
    	//String result = String.format("%.2f", cal);
    	//double result  = (double)Math.round(cal);
    	DecimalFormat df = new DecimalFormat("#.##");      
    	double result = Double.valueOf(df.format(cal));
    	return result;
    }
    
    public class JsonTask extends AsyncTask<String, String, String>{
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();

    		pd = new ProgressDialog(MainActivity.this);
    		pd.setMessage("Please wait");
    		pd.setCancelable(false);
    		pd.show();
    	}

    	@Override
    	protected String doInBackground(String... arg) {

    		HttpURLConnection connection = null;
    		BufferedReader reader = null;

    		try {
    			URL url = new URL(arg[0]);
    			connection = (HttpURLConnection) url.openConnection();
    			connection.connect();

    			InputStream stream = connection.getInputStream();

    			reader = new BufferedReader(new InputStreamReader(stream));

    			StringBuffer buffer = new StringBuffer();
    			String line = "";

    			while ((line = reader.readLine()) != null) {
    				buffer.append(line + "\n");
    				Log.d("Response: ", "> " + line);
    			}
    			currencyString = buffer.toString();
    			return buffer.toString();
			//Exception Handling
			}
    		return null;
    	}

	protected void onPostExecute(String result) {
    		super.onPostExecute(result);

    		if (pd.isShowing()) {
    			pd.dismiss();
    		}

    		try {
    		jsonOBJ = new JSONObject(currencyString);
    		// get time stamp and display date
    		int timestamp = jsonOBJ.getInt("timestamp");
    		date = getDate(timestamp);
    		txtDate.setText(date);
		
		String AUD = jsonOBJ.getJSONObject("quotes").getString("USDAUD");    		
    		audValue = Double.parseDouble(AUD);
    			
    		String USD = jsonOBJ.getJSONObject("quotes").getString("USDUSD");
    		usdValue = Double.parseDouble(USD);
		//Continue for rest of currencies
		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    	}
	
    //Function to save currency data
    public void writeToFile(String data) {
        try {
        	FileOutputStream fileout = openFileOutput("CurrencyData.txt", MODE_PRIVATE);
        	OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        	outputWriter.write(data);
        	outputWriter.close();
        	 
        	//display file saved message      	
        	MessageBox("New Currency Data Saved Successfully");
        	
        }
        catch (IOException e) {
        	MessageBox(e.toString());
        	e.printStackTrace();
        } 
    }
    
        
        
