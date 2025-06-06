import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})

/**
 * Translator class
 */
export class Localizer {

    private msg: any;
    private lngs: any;
    private hasLng: boolean = false;

    getLngs() {
        return this.lngs;
    }

    add(translator: {}, lng: string | null = null) {
        if (lng == null) {
            let aux = { ...this.msg, ...translator }
            this.msg = aux;
        } else {
            let aux = { ...this.msg[lng], ...translator }
            this.msg[lng] = aux;
        }
    }

    addLngs(newlangs: {}) {
        Object.keys(newlangs).forEach((k: any) => {
            this.msg[k] = {}
        })
        let aux = { ...this.lngs, ...newlangs }
        this.lngs = newlangs
        this.hasLng = true
    }

    get(key: string, lng: string | null = null, fallback: string = "", force: boolean = false) {
        let aux = this.msg
        if (force) {
            if (!va(lng)) {
                Object.keys(aux).forEach((k: string) => {
                    aux[k.toLowerCase()] = aux[k]
                })
                key = key.toLowerCase();
            } else {
                Object.keys(aux).forEach((k: string) => {
                    if (aux.length) {
                        Object.keys(aux[k]).forEach((k2: string) => {
                            aux[k][k2.toLowerCase()] = aux[k][k2]
                        })
                        aux[k.toLowerCase()] = aux[k]
                    } else {
                        aux[k.toLowerCase()] = aux[k]
                    }
                })
                key = key.toLowerCase();
                lng = (lng as string).toLowerCase()
            }
        }
        if (!va(lng)) {
            if (aux[key] != undefined) {
                return aux[key];
            } else {
                return fallback;
            }
        } else {
            lng = lng as string
            if (!va(aux[lng])) {
                console.error("Language does not exist")
                return fallback;
            }
            if (aux[lng][key] != undefined) {
                return aux[lng][key];
            } else {
                return fallback;
            }
        }
    }
    /**
     * Set translator
     * @param translator - set the translator as a json
     * @param lngs - 
     */
    set(translator: any, lngs: {} | null = null) {
        this.msg = {};
        if (lngs != null) {
            this.hasLng = true
            this.lngs = lngs
            Object.keys(lngs).forEach((k: any) => {
                this.msg[k] = translator[k];
            })
        } else {
            this.msg = translator
        }
    }

}

/**
 * Function made to apply css easily
 * 
 * @param target - HTMLElement or NodeListOf<HTMLElement> to edit
 * @param options - css to apply
 * 
 * NOTE: Use style names like js does (ex. use pointerEvents instead of pointer-events)
 * EX. css(document.getElementById('target') as HTMLElement,{ bacgroundColor: "#fff"})
 * EX2. css(document.querySelectorAll('.style'),{ bacgroundColor: "#000", position: "fixed"})
 */
export function css(target: HTMLElement | NodeListOf<HTMLElement>, options: any) {
    if (target instanceof NodeList) {
        target.forEach((elem: HTMLElement) => {
            Object.keys(options).forEach((key: any) => {
                elem.style[key] = options[key]
            });
        })
    } else {
        Object.keys(options).forEach((key: any) => {
            target.style[key] = options[key]
        });
    }
}

/**
 * 
 * @param item 
 * @returns true if not empty
 */
export function va(item: any) {
    if (item == null) {
        return false;
    }

    if (typeof item === "object") {
        if (Object.getPrototypeOf(item) === Object.prototype && Object.entries(item).length === 0) {
            return false;
        }
        if (Array.isArray(item) && item.length === 0) {
            return false;
        }
    }

    if (typeof item === "string" && item.trim() === "") {
        return false;
    }

    return true;
}

/**
 * 
 * Applies a pop up effect to all elements on the DOM that are not visible, customizable
 * 
 * NOTE: use either transition all or transition transform, opacity for better results when using default
 * 
 * @param selector query selector to apply
 * @param style class added when an element is shown (empty for default popup)
 * @param once true if effect disappears after shown
 * 
 * @author DanjeDev <danjedev@gmail.com>
 */
export function popUpOnScroll(selector: string, style: string = "", once: boolean = false) {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) { // is cisible
                if (va(style)) {
                    entry.target.className = entry.target.className + " " + style;
                } else {
                    css(entry.target as HTMLElement,{
                        transform: "translateY(0)",
                        scale: "1",
                        opacity: "1"
                    })
                }
                if (once) {
                    observer.unobserve(entry.target);
                }
            } else { // isn't visible
                if (!va(style)){
                    css(entry.target as HTMLElement,{
                        transform: "translateY(30px)",
                        opacity: "0",
                        scale: ".5"
                    })
                }
            }
        });
    });

    ($$(selector) as NodeListOf<HTMLElement>).forEach(el => observer.observe(el));
}

/**
 * 
 * Randomizes an array
 * 
 * @param arr array
 * @returns array
 * 
 * @author DanjeDev <danjedev@gmail.com>
 */
export function shuffleArray(arr: any[]) {
    let array = [...arr]
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

/**
 * Class made to shorten document. functions
 */
export class DOM {
    /**
     * 
     * Create a new element
     * 
     * @param tag HTML Tag
     * @param id (Optional) id of the element created
     * @param parent (Optional) parent to append element
     * @returns HTMLElement
     */
    public static new<K extends keyof HTMLElementTagNameMap>(tag: K, id: string | null = null, parent: string | null | HTMLElement | NodeListOf<HTMLElement> = null): HTMLElementTagNameMap[K] {
        const elem = document.createElement(tag);
        if (va(id)) {
            elem.id = id as string
        }
        if (va(parent)) {
            if (parent instanceof HTMLElement) {
                parent.appendChild(elem)
            } else if (parent instanceof NodeList) {
                parent.forEach((e: any) => {
                    (e as HTMLElement).appendChild(elem)
                })
            } else {
                const parentElem = DOM.g(parent as string)
                if (parentElem instanceof NodeList) {
                    parentElem.forEach((e: any) => {
                        (e as HTMLElement).appendChild(elem)
                    })
                } else {
                    (parentElem as HTMLElement).appendChild(elem)
                }
            }
        }
        return elem;
    }
    /**
     * @see $$
     */
    public static g(selector: string) {
        return $$(selector)
    }

    /**
     * @see $$
     */
    public static get(selector: string) {
        return $$(selector)
    }
}

/**
 * Class made to manage events with ease
 */
export class DaEvent {
    private static events: { name: string, event: EventListener }[] = []

    /**
     * 
     * Gets all custom events
     * 
     * @returns List of all events as {name: string, event: EventListener} 
     */
    public static getAllCustomEvents() {
        return DaEvent.events
    }

    /**
     * 
     * Create a new listener
     * 
     * @param eventName name
     * @param callback function to execute
     * @param remove remove after use (optional)
     */
    public static listen(eventName: string, callback: () => void, remove: boolean = false) {
        if (remove) {
            const ev = () => {
                callback;
                window.removeEventListener(eventName, ev)
            }
            window.addEventListener(eventName, ev)
            DaEvent.events.push({
                name: eventName,
                event: ev
            })
        } else {
            window.addEventListener(eventName, callback);
            DaEvent.events.push({
                name: eventName,
                event: callback
            })
        }
    }

    /**
     * 
     * Throws a new event, all listeners to that name will trigger
     * 
     * @param eventName name
     * @param detail (optional) detail
     */
    public static shoot(eventName: string, detail?: any) {
        const event = new CustomEvent(eventName, { detail });
        window.dispatchEvent(event);
    }

    /**
     * stops listening
     * @param eventName name
     */
    public static stop(eventName: string) {
        for (let i = DaEvent.events.length - 1; i >= 0; i--) {
            if (DaEvent.events[i].name === eventName) {
                window.removeEventListener(DaEvent.events[i].name, DaEvent.events[i].event);
                DaEvent.events.splice(i, 1);
            }
        }
    }

    /**
     * Stops all current listeners
     */
    public static stopAll() {
        DaEvent.events.forEach(event => {
            window.removeEventListener(event.name, event.event);
        });
        DaEvent.events.splice(0, DaEvent.events.length);
    }
}


/**
 * 
 * Shortends query selector all, returns only one element if able, returns null if nothing found
 * 
 * @param selector (query selector)
 * @returns HTMLElement | NodeListOf<HTMLElement> | null
 */
export function $$(selector: string) {
    const elems = document.querySelectorAll(selector)
    switch (elems.length) {
        case 0:
            return null;
        case 1:
            return elems[0];
        default:
            return elems;
    }
}

/**
 * Basic configuration for the header
 */

interface HeaderBasicConfig {
    customClass?: string;
    height?: string;
    primaryColor?: string
    secondaryColor?: string;
    background?: string;
    title?: string;
    titlePosition?: "left" | "center" | "right",
    logo?: string;
    logoSize?: "big" | "medium" | "small"
    logoPosition?: "left" | "center" | "right"
    position?: "fixed" | "relative" | "absolute";
    textColor?: string;
    font?: string;
    fontSize?: string;
    alignment?: "top" | "center" | "bottom";
    justify?: "left" | "center" | "right" | "around" | "between";
    display?: "flex" | "grid"
}

/**
 * Advanced configuration for the header
 */
interface HeaderAdvancedConfig {
    logoHref?: string;
    logoTarget?: "new" | "current";
    headerActiveRoutes?: string[] | null
}

/**
 * Buttons for the header
 */
interface HeaderButton {
    text?: string;
    action?: () => void;
    id?: string
    class?: string
    position?: "left" | "center" | "right"
}

/**
 * Creates a simple but fully customisable header structure and adds it to the DOM
 * @param basic Basic options @see HeaderBasicConfig
 * @param advanced Advanced options @see HeaderAdvancedConfig
 * @param buttons Buttons @see HeaderButton
 * @returns HTMLElement of header
 * @author DanjeDev <danjedev@gmail.com>
 */
export function generateHeader(basic: HeaderBasicConfig = {}, advanced: HeaderAdvancedConfig = {}, buttons: { [key: string]: HeaderButton } = {}) {
    const t: Localizer = new Localizer();
    let logo
    t.set({
        B: {
            left: "start",
            right: "end",
            around: "space-around",
            between: "space-between",
            bottom: "end",
        },
        L: {
            left: "0",
            right: "100%",
            center: "50%",
            new: "_blank",
            current: "_self",
        }
    }, {
        B: "basic",
        L: "logo"
    })
    const def: Required<HeaderBasicConfig> = {
        customClass: "",
        height: "10dvh",
        primaryColor: "#FFF",
        secondaryColor: "#F8F8F8",
        background: "#ffffff",
        title: "",
        titlePosition: "center",
        logo: "",
        logoSize: "big",
        logoPosition: "left",
        position: "fixed",
        textColor: "#000",
        font: "",
        fontSize: "24px",
        alignment: "center",
        justify: "center",
        display: "flex"
    }
    const adv: Required<HeaderAdvancedConfig> = {
        logoHref: "",
        logoTarget: "current",
        headerActiveRoutes: null
    }
    const a = { ...adv, ...advanced }
    const b = { ...def, ...basic }
    const header = DOM.new("header", "header", "body")
    /**
     *      BASIC CONFIGURATION
     */
    header.className = b.customClass;
    css(header, {
        width: "100dvw",
        height: b.height,
        position: b.position,
        top: "0",
        left: "0",
        color: b.textColor,
        font: b.font,
        fontSize: b.fontSize,
        display: "flex",
        zIndex: "100",
        alignItems: "center",
        justifyContent: "space-around",
        flexDirection: "row"
    });
    css(document.body, {
        marginTop: b.height
    })

    const left = DOM.new("div", "header_left", header)
    const center = DOM.new("div", "header_center", header)
    const right = DOM.new("div", "header_right", header)

    const pos = $$('[id^="header_"]') as NodeListOf<HTMLElement>

    css(pos, {
        position: "relative",
        width: "30%",
        height: "100%",
        alignItems: t.get(b.alignment, "B", b.alignment),
        justifyContent: t.get(b.justify, "B", b.justify),
        display: b.display,
        flexDirection: "row"
    })

    if (b.background?.startsWith("#")) {
        css(header, {
            backgroundColor: b.background,
        })
    } else {
        css(header, {
            backgroundImage: `url(${b.background})`,
            backgroundPosition: "center",
            backgroundSize: "cover"
        })
    }
    if (va(b.logo)) {
        logo = DOM.new("div", "logo", `#header_${b.logoPosition}`)
        const logoimg = DOM.new(b.logo.includes(".") ? "img" : "h1", "logo_img", logo)
        if (logoimg instanceof HTMLImageElement) {
            logoimg.src = b.logo
            css(logoimg, {
                display: "block",
            })
        } else {
            logoimg.textContent = b.logo
            css(logoimg, {
                margin: "0",
                display: "flex",
                justifyContent: "center",
                alignItems: "center"
            })
        }
        css(logo, {
            height: "90%",
            display: "inline-block",
            left: t.get(b.logoPosition, "L", "0")
        })
        css(logoimg, {
            height: "100%",
            width: "auto",
        })
        if (b.logoPosition != b.justify) {
            css(logo, {
                position: "absolute"
            })
            if (b.logoPosition != "left") {
                css(logo, {
                    transform: "translateX(-100%)"
                })
            }
        }
    }
    if (va(b.title)) {
        const tit = DOM.new("h1", "title", `#header_${b.titlePosition}`)
        css(tit, {
            margin: "0",
            padding: "0",
            color: b.textColor
        })
        tit.innerHTML = b.title
    }

    /**
     *      ADVANCED CONFIGURATION
     */

    if (va(a.logoHref) && va(b.logo)) {
        const logohref = DOM.new("a", null, logo)
        logohref.href = a.logoHref
        css(logohref, {
            display: "block",
            width: "100%",
            height: "100%",
            transform: "translateY(-100%)"
        })
        logohref.target = t.get(a.logoTarget, "L", "_self")
    }
    if (va(buttons)) {
        Object.keys(buttons).forEach((b: string) => {
            generateHeaderButton(buttons[b])
        })
    }
    if (va(a.headerActiveRoutes)) {
        const event = () => {
            let active: boolean = false
            a.headerActiveRoutes?.forEach(route => {
                if (route == "/") {
                    if (window.location.href == "/") {
                        active = true
                    }
                } else {
                    if (window.location.href.includes(route)) {
                        active = true
                    }
                }
            });
            if (!active) {
                header.remove()
                window.removeEventListener('popstate', event)
                DaEvent.stop('newlocation')
                const body = $$("body") as HTMLElement
                css(body, {
                    marginTop: "0"
                })
            }
        }
        DaEvent.listen('newlocation', event)
        window.addEventListener('popstate', event);
    }
    return header
}
function generateHeaderButton(but: HeaderButton) {
    const basic: Required<HeaderButton> = {
        text: "",
        action: () => { },
        id: "",
        class: "",
        position: "right"
    }
    const c = { ...basic, ...but }
    const parent = `#header_${c.position}`
    const b = DOM.new("div", null, parent)
    b.className = "headerButton"
    css(b, {
        padding: "1em",
        position: "relative",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        margin: "0",
        cursor: "pointer",
        flexDirection: "row"
    })
    if (va(c.id)) {
        b.id = c.id
    }
    if (va(c.class)) {
        b.className = c.class
    }
    if (va(c.text)) {
        b.innerHTML = c.text
    }
    if (va(c.action)) {
        b.addEventListener('click', c.action)
    }
}
