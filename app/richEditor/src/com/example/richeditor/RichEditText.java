package com.example.richeditor;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class RichEditText extends EditText {
	
	public static String COLOR_BLACK = "#000000", COLOR_BLUE = "#3399db";
	
	/**
	 * font settings
	 */
	private boolean font_bold, font_big;
	private String font_color = COLOR_BLACK;
	
	//font flag
	private boolean edting;
	private String last_style;
	private Editable last_editable;
	
	private EditorListner editorListner;

	public RichEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RichEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RichEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		this.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (editorListner != null)
					editorListner.afterEdit();
				if (last_editable == null)
					last_editable = s;
				else {
					last_editable.append(s.toString());
				}
			}
		});
	}
	
	private void ending() {
		
	}
	
	private void reset() {
		font_bold = false;
		font_big = false;
		font_color = COLOR_BLACK;
		edting = false;
	}
	
	private String getStyle() {
		if (isDefaultFont())
			return "<p>";
		StringBuilder builder = new StringBuilder();
		builder.append("<p style=\"")
			.append(font_big ? "font-size:40;" : "")
			.append(font_bold ? "font-weight:bold;" : "")
			.append(COLOR_BLACK.equals(font_color) ? "color:" + font_color: "")
			.append("\">");
		return builder.toString();
	}
	
	private boolean isDefaultFont() {
		return !font_bold && !font_big && COLOR_BLACK.equals(font_color);
	}
	
	public boolean isFont_bold() {
		return font_bold;
	}

	public void setFont_bold(boolean font_bold) {
		this.font_bold = font_bold;
	}

	public boolean isFont_big() {
		return font_big;
	}

	public void setFont_big(boolean font_big) {
		this.font_big = font_big;
	}

	public String getFont_color() {
		return font_color;
	}

	public void setFont_color(String font_color) {
		this.font_color = font_color;
	}

	public void setEditorListner(EditorListner editorListner) {
		this.editorListner = editorListner;
	}

	public interface EditorListner {
		public void afterEdit();
	}
}
