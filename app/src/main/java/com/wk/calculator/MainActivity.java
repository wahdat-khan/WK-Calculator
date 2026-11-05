package com.wk.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends Activity {
    private TextView display, history;
    private String current = "0", operator = "", stored = "", expression = "";
    private boolean fresh = true;
    private final int navy = Color.rgb(16,26,43), gold = Color.rgb(214,174,82), white = Color.WHITE;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(navy); getWindow().setNavigationBarColor(navy);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(18),dp(20),dp(18),dp(14)); root.setBackgroundResource(com.wk.calculator.R.drawable.wk_calculator_background);
        TextView brand = new TextView(this); brand.setText("WK  CALCULATOR"); brand.setTextColor(gold); brand.setTextSize(18); brand.setTypeface(null,Typeface.BOLD); brand.setGravity(Gravity.CENTER); brand.setPadding(0,dp(12),0,dp(24)); root.addView(brand);
        LinearLayout screen = new LinearLayout(this); screen.setOrientation(LinearLayout.VERTICAL); screen.setGravity(Gravity.BOTTOM|Gravity.RIGHT); screen.setPadding(dp(12),dp(12),dp(12),dp(24));
        history = new TextView(this); history.setTextColor(0xFF9BA8BA); history.setTextSize(17); history.setGravity(Gravity.RIGHT); screen.addView(history,new LinearLayout.LayoutParams(-1,dp(35)));
        display = new TextView(this); display.setText("0"); display.setTextColor(white); display.setTextSize(46); display.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); display.setSingleLine(true); screen.addView(display,new LinearLayout.LayoutParams(-1,dp(90)));
        root.addView(screen,new LinearLayout.LayoutParams(-1,0,1));
        String[][] keys = {{"AC","⌫","%","÷"},{"7","8","9","×"},{"4","5","6","−"},{"1","2","3","+"},{"±","0",".","="}};
        GridLayout grid = new GridLayout(this); grid.setColumnCount(4); grid.setRowCount(5);
        for (String[] row: keys) for (String key: row) {
            Button btn = new Button(this); btn.setText(key); btn.setTextSize(23); btn.setAllCaps(false); btn.setTypeface(null,Typeface.BOLD);
            boolean special = "÷×−+=".contains(key) || key.equals("AC") || key.equals("⌫") || key.equals("%");
            btn.setTextColor(special ? navy : white); btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(key.equals("=")?gold:(special?0xFFD6AE52:0xFF26354B)));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(); lp.width=0; lp.height=dp(72); lp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f); lp.setMargins(dp(4),dp(4),dp(4),dp(4)); grid.addView(btn,lp);
            btn.setOnClickListener(v -> press(key));
        }
        root.addView(grid); setContentView(root);
    }
    private void press(String k) {
        if (k.equals("AC")) { current="0"; stored=""; operator=""; expression=""; fresh=true; history.setText(""); }
        else if (k.equals("⌫")) { if (!fresh && current.length()>1) current=current.substring(0,current.length()-1); else current="0"; }
        else if (k.equals(".")) { if (fresh) {current="0.";fresh=false;} else if(!current.contains(".")) current+="."; }
        else if (k.equals("±")) { if(!current.equals("0")) current=current.startsWith("-")?current.substring(1):"-"+current; }
        else if (k.equals("%")) { try { current=fmt(new BigDecimal(current).divide(new BigDecimal("100"),12,RoundingMode.HALF_UP)); } catch(Exception e){} }
        else if ("÷×−+".contains(k)) { if(!operator.isEmpty() && !fresh) calculate(); stored=current; operator=k; expression=stored+" "+k; history.setText(expression); fresh=true; }
        else if (k.equals("=")) { if(!operator.isEmpty()) { String before=stored+" "+operator+" "+current; calculate(); history.setText(before+" ="); operator=""; stored=""; fresh=true; } }
        else { if(fresh || current.equals("0")) current=k; else if(current.length()<15) current+=k; fresh=false; }
        display.setText(current);
    }
    private void calculate() { try { BigDecimal a=new BigDecimal(stored), b=new BigDecimal(current), r;
        switch(operator) { case "+": r=a.add(b); break; case "−": r=a.subtract(b); break; case "×": r=a.multiply(b); break; case "÷": if(b.compareTo(BigDecimal.ZERO)==0){current="Error";return;} r=a.divide(b,10,RoundingMode.HALF_UP); break; default:return; }
        current=fmt(r);
    } catch(Exception e) { current="Error"; } }
    private String fmt(BigDecimal n) { String s=n.stripTrailingZeros().toPlainString(); return s.length()>16?n.round(new java.math.MathContext(12)).stripTrailingZeros().toPlainString():s; }
    private int dp(int v) { return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
}
