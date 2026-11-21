package cl.therionlabs.prospectos.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EventoInfo(String id, String estado) {
}
