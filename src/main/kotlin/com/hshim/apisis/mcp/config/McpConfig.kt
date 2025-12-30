package com.hshim.apisis.mcp.config

import com.hshim.apisis.common.model.APIInfoResponse
import com.hshim.apisis.common.util.APIInfoComponent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import java.util.function.Function

@Configuration
class McpConfig {

    data class ApiFilter(val category: String? = null)

    @Bean
    @Description("Get information about available APIs, optionally filtered by category. Returns a list of API definitions including URL, method, and schema.")
    fun getApiInfo(apiInfoComponent: APIInfoComponent): Function<ApiFilter, List<APIInfoResponse>> {
        return Function { filter ->
            val all = apiInfoComponent.getAPIInfos()
            if (!filter.category.isNullOrBlank()) {
                all.filter { it.category.equals(filter.category, ignoreCase = true) }
            } else {
                all
            }
        }
    }
}
