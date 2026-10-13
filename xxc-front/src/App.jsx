import { useEffect } from 'react'
import { Routes, Route, useLocation } from 'react-router-dom'
import { AnimatePresence } from 'framer-motion'
import Navbar from './components/layout/Navbar'
import Footer from './components/layout/Footer'
import Home from './pages/Home'
import Portfolio from './pages/Portfolio'
import Products from './pages/Products'
import Certificates from './pages/Certificates'
import About from './pages/About'
import Contact from './pages/Contact'
import { useLang } from './i18n/index.jsx'

const PAGE_TITLE_KEYS = {
  '/portfolio': 'portfolio',
  '/products': 'products',
  '/certificates': 'certificates',
  '/about': 'about',
  '/contact': 'contact',
}

export default function App() {
  const location = useLocation()
  const { t } = useLang()

  useEffect(() => {
    const key = PAGE_TITLE_KEYS[location.pathname]
    document.title = key
      ? `${t.nav[key]} — X&X Ceramics`
      : `X&X Ceramics — ${t.meta.tagline}`
  }, [location.pathname, t])

  useEffect(() => { window.scrollTo(0, 0) }, [location.pathname])

  return (
    <>
      <Navbar />
      <AnimatePresence mode="wait">
        <Routes location={location} key={location.pathname}>
          <Route path="/" element={<Home />} />
          <Route path="/portfolio" element={<Portfolio />} />
          <Route path="/products" element={<Products />} />
          <Route path="/certificates" element={<Certificates />} />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
        </Routes>
      </AnimatePresence>
      <Footer />
    </>
  )
}
