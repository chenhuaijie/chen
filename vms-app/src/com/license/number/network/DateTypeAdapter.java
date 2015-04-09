package com.license.number.network;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

public class DateTypeAdapter implements JsonSerializer<Date>,
		JsonDeserializer<Date> {

	// TODO: migrate to streaming adapter

	private final DateFormat enUsFormat;
	private final DateFormat localFormat;
	private final DateFormat bakEnUsFormat;
	private final DateFormat bakLocalFormat;
	private final DateFormat iso8601Format;

	public DateTypeAdapter(String datePattern, String backDatePattern) {
		this(new SimpleDateFormat(datePattern, Locale.US),
				new SimpleDateFormat(datePattern), new SimpleDateFormat(
						backDatePattern, Locale.US), new SimpleDateFormat(
						backDatePattern));
	}

	public DateTypeAdapter(DateFormat enUsFormat, DateFormat localFormat,
			DateFormat bakEnUsFormat, DateFormat bakLocalFormat) {
		this.enUsFormat = enUsFormat;
		this.localFormat = localFormat;
		this.bakEnUsFormat = bakEnUsFormat;
		this.bakLocalFormat = bakLocalFormat;
		this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
				Locale.US);
		this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	// These methods need to be synchronized since JDK DateFormat classes are
	// not thread-safe
	// See issue 162
	public JsonElement serialize(Date src, Type typeOfSrc,
			JsonSerializationContext context) {
		synchronized (localFormat) {
			String dateFormatAsString = enUsFormat.format(src);
			return new JsonPrimitive(dateFormatAsString);
		}
	}

	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		if (!(json instanceof JsonPrimitive)) {
			throw new JsonParseException("The date should be a string value");
		}
		Date date = deserializeToDate(json);
		if (typeOfT == Date.class) {
			return date;
		} else {
			throw new IllegalArgumentException(getClass()
					+ " cannot deserialize to " + typeOfT);
		}
	}

	private Date deserializeToDate(JsonElement json) {
		synchronized (localFormat) {
			try {
				return localFormat.parse(json.getAsString());
			} catch (ParseException ignored) {
//				ignored.printStackTrace();
			}
			try {
				return enUsFormat.parse(json.getAsString());
			} catch (ParseException ignored) {
//				ignored.printStackTrace();
			}
			try {
				return bakLocalFormat.parse(json.getAsString());
			} catch (ParseException ignored) {
//				ignored.printStackTrace();
			}
			try {
				return bakEnUsFormat.parse(json.getAsString());
			} catch (ParseException ignored) {
//				ignored.printStackTrace();
			}

			try {
				return iso8601Format.parse(json.getAsString());
			} catch (ParseException e) {
				e.printStackTrace();
				throw new JsonSyntaxException(json.getAsString(), e);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DateTypeAdapter.class.getSimpleName());
		sb.append('(').append(localFormat.getClass().getSimpleName())
				.append(')');
		return sb.toString();
	}

}
