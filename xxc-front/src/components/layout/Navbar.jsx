import { useState, useEffect } from 'react'
import { Link, NavLink, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useLang } from '../../i18n/index.jsx'
import Icon from '../ui/Icon'

const LANGS = ['uz', 'ru', 'en']

// Pages whose hero is dark and sits directly under the transparent header
const DARK_HERO_PATHS = ['/']

export default function Navbar() {
  const { pathname } = useLocation()
  // The menu remembers the page it was opened on, so navigating away closes it
  const [openOn, setOpenOn] = useState(null)
  const open = openOn === pathname
  const [scrolled, setScrolled] = useState(() => window.scrollY > 20)
  const { lang, t, changeLang } = useLang()

  const links = [
    { to: '/', label: t.nav.home },
    { to: '/portfolio', label: t.nav.portfolio },
    { to: '/products', label: t.nav.products },
    { to: '/certificates', label: t.nav.certificates },
    { to: '/about', label: t.nav.about },
  ]

  const solid = scrolled || open
  const onDark = !solid && DARK_HERO_PATHS.includes(pathname)
  const c = onDark
    ? { brand: '#F6F8F5', accent: '#E39A7C', link: '#C9DDD8', active: '#FFFFFF', muted: '#8FC1B5', sep: 'rgba(246,248,245,0.3)' }
    : { brand: '#2F6F73', accent: '#B4552F', link: '#3E5A60', active: '#2F6F73', muted: '#566E73', sep: '#C5D1CD' }

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  useEffect(() => {
    document.body.style.overflow = open ? 'hidden' : ''
    if (!open) return () => { document.body.style.overflow = '' }
    const onKey = (e) => { if (e.key === 'Escape') setOpenOn(null) }
    window.addEventListener('keydown', onKey)
    return () => {
      document.body.style.overflow = ''
      window.removeEventListener('keydown', onKey)
    }
  }, [open])

  return (
    <>
      <header
        className="fixed top-0 left-0 right-0 z-50 transition-all duration-300"
        style={{
          background: solid ? 'rgba(246,248,245,0.95)' : 'transparent',
          backdropFilter: solid ? 'blur(12px)' : 'none',
          borderBottom: solid ? '1px solid #D5DFDB' : '1px solid transparent',
        }}
      >
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16 md:h-20">
            {/* Logo */}
            <Link
              to="/"
              className="font-heading text-xl md:text-2xl font-bold transition-colors duration-300"
              style={{ color: c.brand, fontFamily: "'Playfair Display', serif" }}
            >
              X&X <span className="transition-colors duration-300" style={{ color: c.accent }}>Ceramics</span>
            </Link>

            {/* Desktop nav */}
            <nav aria-label={t.nav.main} className="hidden lg:flex items-center gap-7">
              {links.map(({ to, label }) => (
                <NavLink
                  key={to}
                  to={to}
                  end={to === '/'}
                  className="relative py-1 text-sm font-medium transition-colors duration-200 hover:opacity-80"
                  style={({ isActive }) => ({ color: isActive ? c.active : c.link })}
                >
                  {({ isActive }) => (
                    <>
                      {label}
                      {isActive && (
                        <span className="absolute left-0 right-0 -bottom-0.5 h-0.5 rounded-full" style={{ background: c.accent }} />
                      )}
                    </>
                  )}
                </NavLink>
              ))}
            </nav>

            {/* Right: Lang switcher + CTA + Burger */}
            <div className="flex items-center gap-3">
              {/* Language switcher */}
              <div role="group" aria-label={t.nav.language} className="flex items-center gap-0.5">
                {LANGS.map((l, i) => (
                  <span key={l} className="flex items-center">
                    <button
                      type="button"
                      onClick={() => changeLang(l)}
                      aria-pressed={lang === l}
                      lang={l}
                      className="text-xs uppercase px-1.5 py-1 rounded transition-colors"
                      style={{
                        color: lang === l ? c.active : c.muted,
                        fontWeight: lang === l ? 700 : 500,
                      }}
                    >
                      {l}
                    </button>
                    {i < LANGS.length - 1 && (
                      <span aria-hidden="true" style={{ color: c.sep, fontSize: '10px' }}>|</span>
                    )}
                  </span>
                ))}
              </div>

              <Link
                to="/contact"
                className="hidden lg:inline-flex items-center px-5 py-2.5 text-sm font-medium rounded-lg transition-all duration-200 hover:opacity-90 hover:-translate-y-0.5"
                style={onDark ? { background: '#B4552F', color: '#FFFFFF' } : { background: '#2F6F73', color: '#FFFFFF' }}
              >
                {t.nav.contact}
              </Link>

              <button
                type="button"
                onClick={() => setOpenOn(open ? null : pathname)}
                className="lg:hidden p-2 -mr-2 rounded-md transition-colors"
                style={{ color: c.brand }}
                aria-label={open ? t.nav.menuClose : t.nav.menuOpen}
                aria-expanded={open}
                aria-controls="mobile-menu"
              >
                <Icon name={open ? 'close' : 'menu'} size={26} strokeWidth={2} />
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Mobile menu */}
      <AnimatePresence>
        {open && (
          <motion.div
            id="mobile-menu"
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="fixed inset-x-0 bottom-0 top-16 md:top-20 lg:hidden flex flex-col"
            style={{ background: '#F6F8F5', zIndex: 40 }}
          >
            <nav aria-label={t.nav.main} className="flex flex-col items-center justify-center flex-1 gap-8 text-center">
              {links.map(({ to, label }) => (
                <NavLink
                  key={to}
                  to={to}
                  end={to === '/'}
                  onClick={() => setOpenOn(null)}
                  className={({ isActive }) =>
                    `text-2xl font-medium transition-colors ${isActive ? 'text-[#2F6F73]' : 'text-[#1B2A2F]'}`
                  }
                  style={{ fontFamily: "'Playfair Display', serif" }}
                >
                  {label}
                </NavLink>
              ))}
              <Link
                to="/contact"
                onClick={() => setOpenOn(null)}
                className="mt-4 px-8 py-3 text-white text-lg rounded-lg"
                style={{ background: '#2F6F73' }}
              >
                {t.nav.contact}
              </Link>
            </nav>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  )
}
