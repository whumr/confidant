package com.example.richeditor;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
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
	private String last_style = "";
	private int last_start, last_count;
	private Spanned last_spanned;
	
	private EditorListner editorListner;
	
	private EditorStatus status = EditorStatus.none;
	
	private enum EditorStatus {
		none, edit, delete, insert
	}

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
				System.out.println(s.toString() + "  " + start + "   " + before + "   " + count);
				if (status == EditorStatus.none) {
					last_start = start;
					last_count = count;
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (!isDefaultFont()) {
					if (status == EditorStatus.none) {
						status = EditorStatus.insert;
						String style = getStyleStart();
//						if (!style.equals(last_style)) {
							
							last_spanned = Html.fromHtml(getStyleStart() + 
									s.subSequence(last_start, last_start + last_count).toString());
							s.replace(last_start, last_start + last_count, last_spanned, 0, last_spanned.length());
							last_style = style;
//						}
						
						
//						s.delete(last_start, last_start + last_count);
//					} else if (status == EditorStatus.delete) {
//						status = EditorStatus.insert;
//						s.insert(getSelectionStart(), last_spanned);
						if (editorListner != null)
							editorListner.afterEdit(s.subSequence(last_start, last_start + last_count).toString());
					} else if (status == EditorStatus.insert) {
						status = EditorStatus.none;
					}
				} else 
					last_style = "";
				
//				if (!edting && !isDefaultFont()) {
//					edting = true;
//////					s.replace(last_start, last_start + last_count, getStyle() + s.subSequence(last_start, last_start + last_count) + "</p>");
//////					s.replace(last_start, last_start + last_count, 
//////							Html.fromHtml(getStyle() + s.subSequence(last_start, last_start + last_count) + "</p>"));
//////					s.insert(getSelectionStart(), Html.fromHtml(getStyle() + s.subSequence(last_start, last_start + last_count) + "</p>"));
//					
//					//s.delete(last_start, last_start + last_count);
//					
//					s.insert(getSelectionStart(), Html.fromHtml(getStyleStart() + 
//							s.subSequence(last_start, last_start + last_count).toString() + getStyleEnd()));
//					if (editorListner != null)
//						editorListner.afterEdit(s.subSequence(last_start, last_start + last_count).toString());
//				} 
//				else if (!isDefaultFont())
//					edting = false;
//				
//				if (last_editable == null)
//					last_editable = s;
//				else {
//					last_editable.append(s.toString());
//				}
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
	
	public String getStyleStart() {
		if (isDefaultFont())
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(COLOR_BLACK.equals(font_color) ? "" : "<font color=\"" + font_color + "\">")
			.append(font_big ? "<big>" : "")
			.append(font_bold ? "<b>" : "");
		return builder.toString();
	}

	public String getStyleEnd() {
		if (isDefaultFont())
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(font_bold ? "</b>" : "")
			.append(font_big ? "</big>" : "")
			.append(COLOR_BLACK.equals(font_color) ? "" : "</font>");
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
		
//		getEditableText().insert(getSelectionStart(), Html.fromHtml(getStyleStart() + "this is a test" + getStyleEnd()));
		
//		moveCursorToVisibleOffset();
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
		public void afterEdit(String str);
	}
}
