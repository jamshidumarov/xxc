import { useState, useEffect } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { portfolioApi } from '../lib/api'
import SectionTitle from '../components/ui/SectionTitle'
import Spinner from '../components/ui/Spinner'
import { useLang } from '../i18n/index.jsx'

const CATEGORY_KEYS = ['', 'LEPKA', 'TYAGA', 'KERAMIKA', 'SUVENIR', 'KULOLCHILIK', 'BUYURTMA']

export default function Portfolio() {
  const { t, field } = useLang()
  const [searchParams, setSearchParams] = useSearchParams()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [selected, setSelected] = useState(null)
  const [imgIdx, setImgIdx] = useState(0)
  const category = searchParams.get('category') || ''

  useEffect(() => {
    setLoading(true)
    setPage(0)
    portfolioApi.getAll({ category: category || undefined, page: 0, size: 12 })
      .then(r => { setItems(r.data.content); setTotalPages(r.data.totalPages) })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [category])

  const openLightbox = (item) => {
    setSelected(item)
    setImgIdx(0)
  }

  const closeLightbox = () => setSelected(null)

  const loadMore = () => {
    const next = page + 1
    portfolioApi.getAll({ category: category || undefined, page: next, size: 12 })
      .then(r => { setItems(prev => [...prev, ...r.data.content]); setPage(next); setTotalPages(r.data.totalPages) })
      .catch(() => {})
  }

  const setCategory = (val) => {
    if (val) setSearchParams({ category: val })
    else setSearchParams({})
  }

  // keyboard navigation for lightbox
  useEffect(() => {
    if (!selected) return
    const images = selected.images || []
    const handler = (e) => {
      if (e.key === 'Escape') closeLightbox()
      if (e.key === 'ArrowLeft') setImgIdx(i => Math.max(0, i - 1))
      if (e.key === 'ArrowRight') setImgIdx(i => Math.min(images.length - 1, i + 1))
    }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [selected])

  return (
    <main className="pt-20">
      <section className="py-16 px-4" style={{ background: '#F0EBE3' }}>
        <div className="max-w-7xl mx-auto">
          <SectionTitle subtitle={t.portfolio.subtitle} title={t.portfolio.title} description={t.portfolio.desc} />
        </div>
      </section>

      <section className="py-12 px-4" style={{ background: '#FAF7F2' }}>
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-wrap gap-2 mb-10">
            {CATEGORY_KEYS.map(key => (
              <button key={key} onClick={() => setCategory(key)}
                className="px-5 py-2 rounded-full text-sm font-medium transition-all duration-200"
                style={category === key
                  ? { background: '#8B5E3C', color: '#FAF7F2' }
                  : { background: '#FFFFFF', color: '#6B4A35', border: '1px solid #E0D5C8' }}>
                {t.products.categories[key]}
              </button>
            ))}
          </div>

          {loading ? <Spinner /> : items.length === 0 ? (
            <div className="text-center py-20" style={{ color: '#A08070' }}>
              <div className="text-5xl mb-4">🏺</div>
              <p>{t.portfolio.empty}</p>
            </div>
          ) : (
            <>
              <motion.div layout className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                <AnimatePresence>
                  {items.map((item, i) => {
                    const images = item.images || []
                    const primaryImg = images.find(x => x.isPrimary) || images[0]
                    return (
                      <motion.div
                        key={item.id} layout
                        initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
                        exit={{ opacity: 0, scale: 0.95 }} transition={{ duration: 0.4, delay: i * 0.04 }}
                        onClick={() => openLightbox(item)}
                        className="group relative rounded-2xl overflow-hidden cursor-pointer aspect-[4/5]"
                        style={{ background: '#E0D5C8' }} whileHover={{ y: -4 }}
                      >
                        {primaryImg?.url ? (
                          <img src={primaryImg.url} alt={field(item, 'title')} className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-6xl">🏺</div>
                        )}

                        {/* Image count badge */}
                        {images.length > 1 && (
                          <div className="absolute top-3 left-3 text-xs px-2 py-0.5 rounded-full text-white"
                            style={{ background: 'rgba(44,24,16,0.65)' }}>
                            📷 {images.length}
                          </div>
                        )}

                        <div className="absolute inset-0 flex items-end p-5 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                          style={{ background: 'linear-gradient(to top, rgba(44,24,16,0.85) 0%, transparent 55%)' }}>
                          <div>
                            <span className="text-xs uppercase tracking-wider" style={{ color: '#C4986B' }}>{item.category}</span>
                            <h3 className="text-white font-semibold mt-1" style={{ fontFamily: "'Playfair Display', serif" }}>
                              {field(item, 'title')}
                            </h3>
                            {item.year && <span className="text-xs mt-1" style={{ color: '#C4986B' }}>{item.year}</span>}
                          </div>
                        </div>
                      </motion.div>
                    )
                  })}
                </AnimatePresence>
              </motion.div>

              {page + 1 < totalPages && (
                <div className="text-center mt-10">
                  <button onClick={loadMore} className="px-8 py-3 rounded-xl text-sm font-medium transition-all hover:-translate-y-0.5"
                    style={{ background: '#8B5E3C', color: '#FAF7F2' }}>
                    {t.common.loadMore}
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </section>

      {/* Lightbox with carousel */}
      <AnimatePresence>
        {selected && (() => {
          const images = selected.images || []
          const currentImg = images[imgIdx]
          return (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
              className="fixed inset-0 z-50 flex items-center justify-center p-4"
              style={{ background: 'rgba(44,24,16,0.88)', backdropFilter: 'blur(8px)' }}
              onClick={closeLightbox}>
              <motion.div initial={{ scale: 0.9 }} animate={{ scale: 1 }} exit={{ scale: 0.9 }}
                onClick={e => e.stopPropagation()}
                className="relative max-w-3xl w-full rounded-2xl overflow-hidden" style={{ background: '#FAF7F2' }}>

                {/* Image area with nav */}
                {images.length > 0 && (
                  <div className="relative">
                    <img src={currentImg?.url} alt={field(selected, 'title')}
                      className="w-full max-h-[55vh] object-cover" />

                    {/* Prev / Next */}
                    {images.length > 1 && (
                      <>
                        <button onClick={() => setImgIdx(i => Math.max(0, i - 1))}
                          disabled={imgIdx === 0}
                          className="absolute left-3 top-1/2 -translate-y-1/2 w-9 h-9 rounded-full flex items-center justify-center text-white disabled:opacity-30"
                          style={{ background: 'rgba(44,24,16,0.65)' }}>‹</button>
                        <button onClick={() => setImgIdx(i => Math.min(images.length - 1, i + 1))}
                          disabled={imgIdx === images.length - 1}
                          className="absolute right-3 top-1/2 -translate-y-1/2 w-9 h-9 rounded-full flex items-center justify-center text-white disabled:opacity-30"
                          style={{ background: 'rgba(44,24,16,0.65)' }}>›</button>

                        {/* Dot indicators */}
                        <div className="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-1.5">
                          {images.map((_, i) => (
                            <button key={i} onClick={() => setImgIdx(i)}
                              className="w-2 h-2 rounded-full transition-all"
                              style={{ background: i === imgIdx ? '#C4986B' : 'rgba(255,255,255,0.5)' }} />
                          ))}
                        </div>
                      </>
                    )}
                  </div>
                )}

                {/* Thumbnails */}
                {images.length > 1 && (
                  <div className="flex gap-2 px-5 pt-3 overflow-x-auto">
                    {images.map((img, i) => (
                      <button key={img.id} onClick={() => setImgIdx(i)}
                        className="w-14 h-14 shrink-0 rounded-lg overflow-hidden transition-all"
                        style={{ border: i === imgIdx ? '2px solid #8B5E3C' : '2px solid transparent', opacity: i === imgIdx ? 1 : 0.5 }}>
                        <img src={img.url} alt="" className="w-full h-full object-cover" />
                      </button>
                    ))}
                  </div>
                )}

                {/* Info */}
                <div className="p-6">
                  <div className="flex items-center gap-3 mb-2">
                    <span className="text-xs uppercase tracking-wider" style={{ color: '#D4843C' }}>{selected.category}</span>
                    {selected.year && <span className="text-xs" style={{ color: '#A08070' }}>{selected.year}</span>}
                    {images.length > 1 && (
                      <span className="text-xs" style={{ color: '#A08070' }}>{imgIdx + 1} / {images.length}</span>
                    )}
                  </div>
                  <h3 className="text-2xl font-bold mb-2" style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}>
                    {field(selected, 'title')}
                  </h3>
                  {field(selected, 'description') && (
                    <p className="text-sm mb-4" style={{ color: '#6B4A35' }}>{field(selected, 'description')}</p>
                  )}
                  <Link to="/contact" onClick={closeLightbox}
                    className="inline-flex items-center gap-2 px-6 py-3 rounded-xl text-sm font-medium text-white"
                    style={{ background: '#8B5E3C' }}>
                    {t.portfolio.orderSimilar}
                  </Link>
                </div>

                <button onClick={closeLightbox}
                  className="absolute top-3 right-3 w-8 h-8 rounded-full flex items-center justify-center text-white"
                  style={{ background: 'rgba(44,24,16,0.6)' }}>✕</button>
              </motion.div>
            </motion.div>
          )
        })()}
      </AnimatePresence>
    </main>
  )
}
