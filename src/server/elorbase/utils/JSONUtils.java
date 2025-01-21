package server.elorbase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONUtils {
	
	/**
	 * Preparar objetos para enviar, mediante la serialización.
	 * 
	 * @param o Cualquier objeto a serializar
	 * @return String con el objeto serializado
	 * @throws JsonProcessingException Excepción de error al parsear a JSON
	 */
	public static String getSerializedString(Object o) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper.writeValueAsString(o);
	}

}
