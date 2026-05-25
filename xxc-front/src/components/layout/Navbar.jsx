import { useState, useEffect } from 'react'
import { Link, NavLink, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useLang } from '../../i18n/index.jsx'

const LANGS = ['uz', 'ru', 'en']

export default function Navbar() {
  const [open, setOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const { pathname } = useLocation()
  const { lang, t, changeLang } = useLang()

  const links = [
    { to: '/', label: t.nav.home },
    { to: '/portfolio', label: t.nav.portfolio },
    { to: '/products', label: t.nav.products },
    { to: '/certificates', label: t.nav.certificates },
    { to: '/about', label: t.nav.about },
  ]

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  useEffect(() => { setOpen(false) }, [pathname])

  useEffect(() => {
    document.body.style.overflow = open ? 'hidden' : ''
    return () => { document.body.style.overflow = '' }
  }, [open])

  return (
    <header
      className="fixed top-0 left-0 right-0 z-50 transition-all duration-300"
      style={{
        background: scrolled ? 'rgba(250, 247, 242, 0.95)' : 'transparent',
        backdropFilter: scrolled ? 'blur(12px)' : 'none',
        borderBottom: scrolled ? '1px solid #E0D5C8' : 'none',
      }}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 md:h-20">
          {/* Logo */}
          <Link
            to="/"
            className="font-heading text-xl md:text-2xl font-bold"
            style={{ color: '#8B5E3C', fontFamily: "'Playfair Display', serif" }}
          >
            X&X <span style={{ color: '#D4843C' }}>Ceramics</span>
          </Link>

          {/* Desktop nav */}
          <nav className="hidden md:flex items-center gap-6">
            {links.map(({ to, label }) => (
              <NavLink
                key={to}
                to={to}
                end={to === '/'}
                className={({ isActive }) =>
                  `text-sm font-medium transition-colors duration-200 ${
                    isActive ? 'text-[#8B5E3C]' : 'text-[#6B4A35] hover:text-[#8B5E3C]'
                  }`
                }
              >
                {label}
              </NavLink>
            ))}
          </nav>

          {/* Right: Lang switcher + CTA + Burger */}
          <div className="flex items-center gap-3">
            {/* Language switcher */}
            <div className="flex items-center gap-0.5">
              {LANGS.map((l, i) => (
                <span key={l} className="flex items-center">
                  <button
                    onClick={() => changeLang(l)}
                    className="text-xs font-semibold uppercase px-1 py-0.5 rounded transition-colors"
                    style={{
                      color: lang === l ? '#8B5E3C' : '#A08070',
                      fontWeight: lang === l ? 700 : 400,
                    }}
                  >
                    {l}
                  </button>
                  {i < LANGS.length - 1 && (
                    <span style={{ color: '#D0C8C0', fontSize: '10px' }}>|</span>
                  )}
                </span>
              ))}
            </div>

            <Link
              to="/contact"
              className="hidden md:inline-flex items-center px-5 py-2.5 text-sm font-medium text-white rounded-lg transition-all duration-200 hover:opacity-90 hover:-translate-y-0.5"
              style={{ background: '#8B5E3C' }}
            >
              {t.nav.contact}
            </Link>

            <button
              onClick={() => setOpen(!open)}
              className="md:hidden p-2 rounded-md"
              style={{ color: '#8B5E3C' }}
              aria-label="Menu"
            >
              <div className="w-6 h-5 flex flex-col justify-between">
                <span className="block h-0.5 transition-all duration-300"
                  style={{ background: '#8B5E3C', transform: open ? 'rotate(45deg) translate(7px, 7px)' : '' }} />
                <span className="block h-0.5 transition-all duration-300"
                  style={{ background: '#8B5E3C', opacity: open ? 0 : 1 }} />
                <span className="block h-0.5 transition-all duration-300"
                  style={{ background: '#8B5E3C', transform: open ? 'rotate(-45deg) translate(7px, -7px)' : '' }} />
              </div>
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="fixed inset-0 top-16 md:hidden flex flex-col"
            style={{ background: '#FAF7F2', zIndex: 40 }}
          >
            <nav className="flex flex-col items-center justify-center flex-1 gap-8 text-center">
              {links.map(({ to, label }) => (
                <NavLink
                  key={to}
                  to={to}
                  end={to === '/'}
                  className={({ isActive }) =>
                    `text-2xl font-medium transition-colors ${isActive ? 'text-[#8B5E3C]' : 'text-[#2C1810]'}`
                  }
                  style={{ fontFamily: "'Playfair Display', serif" }}
                >
                  {label}
                </NavLink>
              ))}
              <Link
                to="/contact"
                className="mt-4 px-8 py-3 text-white text-lg rounded-lg"
                style={{ background: '#8B5E3C' }}
              >
                {t.nav.contact}
              </Link>
              {/* Mobile lang switcher */}
              <div className="flex items-center gap-3 mt-2">
                {LANGS.map(l => (
                  <button
                    key={l}
                    onClick={() => changeLang(l)}
                    className="text-sm font-semibold uppercase px-3 py-1.5 rounded-lg transition-colors"
                    style={{
                      background: lang === l ? '#8B5E3C' : '#F0EBE3',
                      color: lang === l ? '#fff' : '#6B4A35',
                    }}
                  >
                    {l}
                  </button>
                ))}
              </div>
            </nav>
          </motion.div>
        )}
      </AnimatePresence>
    </header>
  )
}
