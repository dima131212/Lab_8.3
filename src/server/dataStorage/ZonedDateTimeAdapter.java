package server.dataStorage;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * Адаптер для преобразования объекта {@link ZonedDateTime} в строку и обратно для сериализации/десериализации в формате XML.
 * Использует стандартный формат {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}.
 */
public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {
	/**
     * Преобразует строковое представление даты и времени в объект {@link ZonedDateTime}.
     * 
     * @param v строка, представляющая дату и время в формате ISO.
     * @return объект {@link ZonedDateTime}, соответствующий строковому представлению.
     */
    @Override
    public ZonedDateTime unmarshal(String v) {
        return ZonedDateTime.parse(v, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
    /**
     * Преобразует объект {@link ZonedDateTime} в строковое представление в формате ISO.
     * 
     * @param v объект {@link ZonedDateTime}, который необходимо преобразовать.
     * @return строковое представление даты и времени в формате ISO.
     */
    @Override
    public String marshal(ZonedDateTime v) {
        return v.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
