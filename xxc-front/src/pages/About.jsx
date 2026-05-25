import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import SectionTitle from '../components/ui/SectionTitle'
import { useLang } from '../i18n/index.jsx'

export default function About() {
  const { t } = useLang()
  const a = t.about

  return (
    <main className="pt-20">
      {/* Hero */}
      <section className="py-24 px-4 text-center" style={{ background: 'linear-gradient(135deg, #2C1810 0%, #5C3D22 100%)' }}>
        <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.7 }} className="max-w-3xl mx-auto">
          <span className="text-sm uppercase tracking-widest font-medium" style={{ color: '#C4986B' }}>{a.badge}</span>
          <h1 className="text-4xl md:text-5xl font-bold text-white mt-3 mb-5" style={{ fontFamily: "'Playfair Display', serif" }}>
            {a.title}
          </h1>
          <p className="text-base md:text-lg" style={{ color: '#C4986B', lineHeight: 1.7 }}>{a.subtitle2}</p>
        </motion.div>
      </section>

      {/* Story */}
      <section className="py-20 px-4" style={{ background: '#FAF7F2' }}>
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <motion.div initial={{ opacity: 0, x: -30 }} whileInView={{ opacity: 1, x: 0 }} viewport={{ once: true }} transition={{ duration: 0.7 }}>
              <SectionTitle subtitle={a.historyBadge} title={a.historyTitle} />
              <div className="mt-6 space-y-4 text-base" style={{ color: '#6B4A35', lineHeight: 1.8 }}>
                {a.story.map((p, i) => <p key={i}>{p}</p>)}
              </div>
            </motion.div>
            <motion.div initial={{ opacity: 0, x: 30 }} whileInView={{ opacity: 1, x: 0 }} viewport={{ once: true }} transition={{ duration: 0.7 }} className="grid grid-cols-2 gap-4">
              {['Xasanboy', 'Xusanboy'].map((name, i) => (
                <div key={name} className="rounded-2xl overflow-hidden aspect-[3/4] flex flex-col" style={{ background: '#F0EBE3', border: '1px solid #E0D5C8' }}>
                  <div className="flex-1 flex items-center justify-center text-7xl">{i === 0 ? '🧑‍🎨' : '👨‍🎨'}</div>
                  <div className="p-4 text-center">
                    <div className="font-semibold" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>{name}</div>
                    <div className="text-xs mt-1" style={{ color: '#A08070' }}>{a.founder}</div>
                  </div>
                </div>
              ))}
            </motion.div>
          </div>
        </div>
      </section>

      {/* Process */}
      <section className="py-20 px-4" style={{ background: '#F0EBE3' }}>
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-14">
            <SectionTitle subtitle={a.processBadge} title={a.processTitle} center />
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {a.process.map((item, i) => (
              <motion.div key={item.step} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: i * 0.1, duration: 0.5 }}
                className="rounded-2xl p-7" style={{ background: '#FFFFFF', border: '1px solid #E0D5C8' }}>
                <span className="text-3xl font-bold" style={{ color: '#E0D5C8', fontFamily: "'Playfair Display', serif" }}>{item.step}</span>
                <h4 className="text-lg font-semibold mt-3 mb-2" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>{item.title}</h4>
                <p className="text-sm" style={{ color: '#6B4A35' }}>{item.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Values */}
      <section className="py-20 px-4" style={{ background: '#8B5E3C' }}>
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-14">
            <span className="text-sm uppercase tracking-widest font-medium" style={{ color: '#C4986B' }}>{a.valuesBadge}</span>
            <h2 className="text-3xl font-bold mt-2 text-white" style={{ fontFamily: "'Playfair Display', serif" }}>{a.valuesTitle}</h2>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {a.values.map((val, i) => (
              <motion.div key={i} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: i * 0.1, duration: 0.5 }}
                className="text-center rounded-2xl p-7" style={{ background: 'rgba(250,247,242,0.08)' }}>
                <div className="text-4xl mb-4">{val.icon}</div>
                <h4 className="font-semibold text-white mb-2" style={{ fontFamily: "'Playfair Display', serif" }}>{val.title}</h4>
                <p className="text-sm" style={{ color: '#C4986B' }}>{val.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 px-4 text-center" style={{ background: '#FAF7F2' }}>
        <div className="max-w-lg mx-auto">
          <h2 className="text-2xl font-bold mb-4" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>{a.ctaTitle}</h2>
          <p className="text-sm mb-6" style={{ color: '#6B4A35' }}>{a.ctaDesc}</p>
          <Link to="/contact" className="inline-flex px-8 py-3 text-white rounded-xl font-medium" style={{ background: '#8B5E3C' }}>
            {a.ctaBtn}
          </Link>
        </div>
      </section>
    </main>
  )
}
