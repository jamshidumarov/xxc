import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion, useInView } from 'framer-motion'
import { portfolioApi } from '../lib/api'
import SectionTitle from '../components/ui/SectionTitle'
import { useLang } from '../i18n/index.jsx'
import Icon from '../components/ui/Icon'

const STATS_VALUES = [10, 500, 350, 15]
const SERVICE_ICONS = ['vase', 'palette', 'flame', 'figurine', 'plant', 'sparkles']

function AnimatedCounter({ target }) {
  const [count, setCount] = useState(0)
  const ref = useRef(null)
  const inView = useInView(ref, { once: true })

  useEffect(() => {
    if (!inView) return
    let start = 0
    const step = target / (2000 / 16)
    const timer = setInterval(() => {
      start += step
      if (start >= target) { setCount(target); clearInterval(timer) }
      else setCount(Math.floor(start))
    }, 16)
    return () => clearInterval(timer)
  }, [inView, target])

  return <span ref={ref}>{count.toLocaleString()}</span>
}

const fadeUp = { initial: { opacity: 0, y: 30 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.6, ease: 'easeOut' } }

export default function Home() {
  const [featured, setFeatured] = useState([])
  const { t, field } = useLang()

  useEffect(() => {
    portfolioApi.getFeatured().then(r => setFeatured(r.data.slice(0, 4))).catch(() => {})
  }, [])

  return (
    <main>
      {/* HERO */}
      <section
        className="relative min-h-screen flex items-center justify-center overflow-hidden"
        style={{ background: 'linear-gradient(135deg, #1B2A2F 0%, #1F4E5A 50%, #2F6F73 100%)' }}
      >
        <div className="absolute inset-0 opacity-10" style={{
          backgroundImage: `radial-gradient(circle at 25% 25%, #8FC1B5 0%, transparent 50%),
                            radial-gradient(circle at 75% 75%, #B4552F 0%, transparent 50%)`,
        }} />
        <div className="relative z-10 text-center px-4 max-w-4xl mx-auto">
          <motion.span {...fadeUp} className="inline-block text-sm font-medium uppercase tracking-widest mb-6" style={{ color: '#8FC1B5' }}>
            {t.hero.badge}
          </motion.span>
          <motion.h1
            initial={{ opacity: 0, y: 40 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.8, delay: 0.1 }}
            className="text-4xl sm:text-5xl md:text-6xl lg:text-7xl font-bold text-white mb-6"
            style={{ fontFamily: "'Playfair Display', serif", lineHeight: 1.1 }}
          >
            {t.hero.title1}<br />
            <span style={{ color: '#E39A7C' }}>{t.hero.title2}</span>
          </motion.h1>
          <motion.p
            initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.7, delay: 0.2 }}
            className="text-lg md:text-xl mb-10 max-w-2xl mx-auto" style={{ color: '#C6E0D9', lineHeight: 1.7 }}
          >
            {t.hero.subtitle}
          </motion.p>
          <motion.div
            initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.3 }}
            className="flex flex-col sm:flex-row gap-4 justify-center"
          >
            <Link to="/portfolio" className="px-8 py-4 text-base font-semibold rounded-xl transition-all duration-200 hover:-translate-y-1 hover:shadow-lg" style={{ background: '#B4552F', color: '#fff' }}>
              {t.hero.viewWorks}
            </Link>
            <Link to="/contact" className="px-8 py-4 text-base font-semibold rounded-xl transition-all duration-200 hover:-translate-y-1" style={{ background: 'transparent', color: '#F6F8F5', border: '2px solid rgba(246,248,245,0.4)' }}>
              {t.hero.order}
            </Link>
          </motion.div>
        </div>
        <motion.div animate={{ y: [0, 8, 0] }} transition={{ repeat: Infinity, duration: 1.8 }} className="absolute bottom-8 left-1/2 -translate-x-1/2" style={{ color: 'rgba(246,248,245,0.5)' }}>
          <svg width="24" height="36" viewBox="0 0 24 36" fill="none" stroke="currentColor" strokeWidth="2">
            <rect x="1" y="1" width="22" height="34" rx="11" />
            <circle cx="12" cy="10" r="3" fill="currentColor" />
          </svg>
        </motion.div>
      </section>

      {/* SERVICES */}
      <section className="py-20 px-4" style={{ background: '#F6F8F5' }}>
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-14">
            <SectionTitle subtitle={t.services.subtitle} title={t.services.title} description={t.services.desc} center />
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {t.services.items.map((service, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, y: 30 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
                transition={{ duration: 0.5, delay: i * 0.08 }} whileHover={{ y: -6, transition: { duration: 0.2 } }}
                className="rounded-2xl p-8 cursor-pointer"
                style={{ background: '#FFFFFF', border: '1px solid #D5DFDB', boxShadow: '0 2px 12px rgba(27,42,47,0.05)' }}
              >
                <div className="w-12 h-12 mb-5 rounded-xl flex items-center justify-center" style={{ background: '#E9EFEC', color: '#2F6F73' }}>
                  <Icon name={SERVICE_ICONS[i]} size={26} />
                </div>
                <h3 className="text-xl font-semibold mb-2" style={{ fontFamily: "'Playfair Display', serif", color: '#1B2A2F' }}>
                  {service.title}
                </h3>
                <p className="text-sm leading-relaxed" style={{ color: '#3E5A60' }}>{service.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* FEATURED PORTFOLIO */}
      {featured.length > 0 && (
        <section className="py-20 px-4" style={{ background: '#E9EFEC' }}>
          <div className="max-w-7xl mx-auto">
            <div className="flex flex-col md:flex-row md:items-end justify-between mb-12 gap-4">
              <SectionTitle subtitle={t.featured.subtitle} title={t.featured.title} />
              <Link to="/portfolio" className="inline-flex items-center gap-1.5 text-sm font-medium underline-offset-4 hover:underline shrink-0" style={{ color: '#2F6F73' }}>
                {t.featured.viewAll} <Icon name="arrowRight" size={16} />
              </Link>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
              {featured.map((item, i) => (
                <motion.div
                  key={item.id}
                  initial={{ opacity: 0, scale: 0.95 }} whileInView={{ opacity: 1, scale: 1 }}
                  viewport={{ once: true }} transition={{ duration: 0.5, delay: i * 0.1 }}
                  className="group relative rounded-2xl overflow-hidden aspect-[4/5]" style={{ background: '#D5DFDB' }}
                >
                  {item.images?.[0]?.url ? (
                    <img src={item.images[0].url} alt={field(item, 'title')} className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center" style={{ background: '#D5DFDB', color: '#566E73' }}><Icon name="vase" size={56} strokeWidth={1.25} /></div>
                  )}
                  <div className="absolute inset-0 flex items-end p-5 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                    style={{ background: 'linear-gradient(to top, rgba(27,42,47,0.85) 0%, transparent 60%)' }}>
                    <div>
                      <p className="text-xs uppercase tracking-wider mb-1" style={{ color: '#8FC1B5' }}>{t.products.categories[item.category] || item.category}</p>
                      <h4 className="text-white font-medium" style={{ fontFamily: "'Playfair Display', serif" }}>
                        {field(item, 'title')}
                      </h4>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* STATS */}
      <section className="py-20 px-4" style={{ background: '#2F6F73' }}>
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
            {t.stats.map((label, i) => (
              <motion.div key={i} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ duration: 0.6 }}>
                <div className="text-4xl md:text-5xl font-bold mb-2" style={{ color: '#F6F8F5', fontFamily: "'Playfair Display', serif" }}>
                  <AnimatedCounter target={STATS_VALUES[i]} />+
                </div>
                <div className="text-sm font-medium" style={{ color: '#D3E8E2' }}>{label}</div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-24 px-4 text-center" style={{ background: '#F6F8F5' }}>
        <div className="max-w-2xl mx-auto">
          <motion.div initial={{ opacity: 0, y: 30 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ duration: 0.7 }}>
            <span className="text-sm uppercase tracking-widest font-medium" style={{ color: '#B4552F' }}>{t.cta.badge}</span>
            <h2 className="text-3xl md:text-4xl font-bold mt-3 mb-5" style={{ fontFamily: "'Playfair Display', serif", color: '#1B2A2F' }}>
              {t.cta.title}
            </h2>
            <p className="mb-8 text-base" style={{ color: '#3E5A60' }}>{t.cta.desc}</p>
            <Link to="/contact" className="inline-flex items-center gap-2 px-10 py-4 text-white font-semibold rounded-xl text-base transition-all duration-200 hover:-translate-y-1 hover:shadow-xl" style={{ background: '#2F6F73' }}>
              {t.cta.btn} <Icon name="arrowRight" size={18} />
            </Link>
          </motion.div>
        </div>
      </section>
    </main>
  )
}
