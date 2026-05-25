import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { certificateApi } from '../lib/api'
import SectionTitle from '../components/ui/SectionTitle'
import Spinner from '../components/ui/Spinner'
import { useLang } from '../i18n/index.jsx'

const TYPE_COLORS = {
  CERTIFICATE: '#8B5E3C',
  AWARD: '#D4843C',
  ACHIEVEMENT: '#5C7A4E',
}

export default function Certificates() {
  const { t, field } = useLang()
  const c = t.certificates
  const [certs, setCerts] = useState([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState(null)

  useEffect(() => {
    certificateApi.getAll().then(r => setCerts(r.data)).catch(() => {}).finally(() => setLoading(false))
  }, [])

  const achievements = certs.filter(x => x.type === 'ACHIEVEMENT')
  const certsList = certs.filter(x => x.type !== 'ACHIEVEMENT')

  return (
    <main className="pt-20">
      <section className="py-16 px-4" style={{ background: '#F0EBE3' }}>
        <div className="max-w-7xl mx-auto">
          <SectionTitle subtitle={c.subtitle} title={c.title} description={c.desc} />
        </div>
      </section>

      {loading ? (
        <div className="py-20"><Spinner /></div>
      ) : certs.length === 0 ? (
        <div className="text-center py-24" style={{ color: '#A08070' }}>
          <div className="text-5xl mb-4">🏅</div>
          <p>{c.empty}</p>
        </div>
      ) : (
        <>
          {achievements.length > 0 && (
            <section className="py-16 px-4" style={{ background: '#FAF7F2' }}>
              <div className="max-w-3xl mx-auto">
                <h3 className="text-2xl font-bold mb-10 text-center" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>
                  {c.achievementsTitle}
                </h3>
                <div className="relative">
                  <div className="absolute left-4 top-0 bottom-0 w-0.5" style={{ background: '#E0D5C8' }} />
                  <div className="space-y-10">
                    {achievements.map((item, i) => (
                      <motion.div key={item.id} initial={{ opacity: 0, x: -20 }} whileInView={{ opacity: 1, x: 0 }}
                        viewport={{ once: true }} transition={{ delay: i * 0.1, duration: 0.5 }} className="pl-12 relative">
                        <div className="absolute left-0 w-8 h-8 rounded-full flex items-center justify-center text-white text-sm font-bold"
                          style={{ background: TYPE_COLORS.ACHIEVEMENT }}>
                          {item.year ? String(item.year).slice(-2) : '✓'}
                        </div>
                        <div className="rounded-xl p-5" style={{ background: '#FFFFFF', border: '1px solid #E0D5C8' }}>
                          {item.year && <span className="text-xs font-medium" style={{ color: '#5C7A4E' }}>{item.year}</span>}
                          <h4 className="font-semibold mt-1" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>
                            {field(item, 'title')}
                          </h4>
                          {item.issuedBy && <p className="text-sm mt-1" style={{ color: '#A08070' }}>{item.issuedBy}</p>}
                          {field(item, 'description') && <p className="text-sm mt-2" style={{ color: '#6B4A35' }}>{field(item, 'description')}</p>}
                        </div>
                      </motion.div>
                    ))}
                  </div>
                </div>
              </div>
            </section>
          )}

          {certsList.length > 0 && (
            <section className="py-16 px-4" style={{ background: '#F0EBE3' }}>
              <div className="max-w-7xl mx-auto">
                <h3 className="text-2xl font-bold mb-10 text-center" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>
                  {c.certsTitle}
                </h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                  {certsList.map((cert, i) => (
                    <motion.div key={cert.id} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }}
                      viewport={{ once: true }} transition={{ delay: i * 0.08, duration: 0.5 }}
                      onClick={() => cert.imageUrl && setSelected(cert)}
                      className={`rounded-2xl overflow-hidden ${cert.imageUrl ? 'cursor-pointer' : ''}`}
                      style={{ background: '#FFFFFF', border: '1px solid #E0D5C8' }}
                      whileHover={cert.imageUrl ? { y: -4 } : {}}>
                      {cert.imageUrl ? (
                        <div className="aspect-[3/4] overflow-hidden">
                          <img src={cert.imageUrl} alt={field(cert, 'title')} className="w-full h-full object-cover transition-transform duration-500 hover:scale-105" />
                        </div>
                      ) : (
                        <div className="aspect-[3/4] flex items-center justify-center" style={{ background: '#F0EBE3' }}>
                          <span className="text-6xl">{cert.type === 'AWARD' ? '🏆' : '📜'}</span>
                        </div>
                      )}
                      <div className="p-5">
                        <div className="flex items-center gap-2 mb-2">
                          <span className="text-xs px-2 py-0.5 rounded-full font-medium text-white"
                            style={{ background: TYPE_COLORS[cert.type] || '#8B5E3C' }}>
                            {c.types[cert.type] || cert.type}
                          </span>
                          {cert.year && <span className="text-xs" style={{ color: '#A08070' }}>{cert.year}</span>}
                        </div>
                        <h4 className="font-semibold text-sm" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>
                          {field(cert, 'title')}
                        </h4>
                        {cert.issuedBy && <p className="text-xs mt-1" style={{ color: '#A08070' }}>{cert.issuedBy}</p>}
                      </div>
                    </motion.div>
                  ))}
                </div>
              </div>
            </section>
          )}
        </>
      )}

      {/* Lightbox */}
      <AnimatePresence>
        {selected && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex items-center justify-center p-4"
            style={{ background: 'rgba(44,24,16,0.9)' }} onClick={() => setSelected(null)}>
            <motion.div initial={{ scale: 0.85 }} animate={{ scale: 1 }} exit={{ scale: 0.85 }}
              onClick={e => e.stopPropagation()} className="relative max-w-lg w-full rounded-2xl overflow-hidden">
              <img src={selected.imageUrl} alt={field(selected, 'title')} className="w-full" />
              <button onClick={() => setSelected(null)}
                className="absolute top-3 right-3 w-8 h-8 rounded-full flex items-center justify-center text-white"
                style={{ background: 'rgba(44,24,16,0.7)' }}>✕</button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </main>
  )
}
