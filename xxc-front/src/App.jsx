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

export default function App() {
  const location = useLocation()

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
