package md.usm.teza.reservare.config

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Forwards SPA client-side routes to index.html so deep links
 * (e.g. /rooms/3?seat=12) work when the UI is served by Spring.
 */
@Controller
class SpaForwardingController {

    @GetMapping("/login", "/my", "/requests", "/rooms", "/rooms/{id:\\d+}")
    fun forward(): String = "forward:/index.html"
}
