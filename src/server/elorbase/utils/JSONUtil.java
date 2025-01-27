package server.elorbase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONUtil {
	
	/**
	 * Preparar objetos para enviar, mediante la serialización.
	 * 
	 * @param o Cualquier objeto a serializar
	 * @return String con el objeto serializado
	 * @throws JsonProcessingException Excepción de error al parsear a JSON
	 */
	public static String getSerializedString(Object o) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper.writeValueAsString(o);
	}
	
	public static <T> T getFromJSON(String json, Class<T> clase) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper.readValue(json, clase);
	}

}
 