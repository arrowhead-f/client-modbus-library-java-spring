package de.twt.client.modbus.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import eu.arrowhead.client.library.config.DefaultSecurityConfig;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;

@Configuration
@EnableWebSecurity
public class ModbusSecurityConfig extends DefaultSecurityConfig {
	
	//=================================================================================================
	// members
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	private ModbusTokenSecurityFilter tokenSecurityFilter;
	private ModbusNotificationAccessControlFilter notificationFilter;
	
	//=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		
		notificationFilter = new ModbusNotificationAccessControlFilter();
		http.addFilterBefore( notificationFilter , SecurityContextHolderAwareRequestFilter.class );
		
		if (sslEnabled && tokenSecurityFilterEnabled) {
			tokenSecurityFilter = new ModbusTokenSecurityFilter();
			http.addFilterAfter(tokenSecurityFilter, SecurityContextHolderAwareRequestFilter.class);			
		}
	}

	//-------------------------------------------------------------------------------------------------
	public ModbusTokenSecurityFilter getTokenSecurityFilter() {
		return tokenSecurityFilter;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ModbusNotificationAccessControlFilter getNotificationFilter() {
		return notificationFilter;
	}
}

