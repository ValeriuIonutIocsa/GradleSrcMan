package com.utils.xml.dom.documents;

import org.w3c.dom.Document;

import com.utils.string.StrUtils;

public record ValidatedDocument(
		Document document,
		boolean validationSuccessful) {

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
