import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { productApi } from '../lib/api'
import SectionTitle from '../components/ui/SectionTitle'
import Spinner from '../components/ui/Spinner'
import { useLang } from '../i18n/index.jsx'
import Icon from '../components/ui/Icon'

const AVAIL_COLORS = {
  AVAILABLE: '#4F7D5C',
  ORDER: '#B4552F',
  SOLD_OUT: '#566E73',
}

const CATEGORY_KEYS = ['', 'LEPKA', 'KERAMIKA', 'SUVENIR', 'KULOLCHILIK', 'TYAGA', 'BUYURTMA']

function ImageCarousel({ images, onClose }) {
  const { t } = useLang()
  const [idx, setIdx] = useState(0)
  const prev = () => setIdx(i => Math.max(0, i - 1))
  const next = () => setIdx(i => Math.min(images.length - 1, i + 1))

  useEffect(() => {
    const handler = (e) => { if (e.key === 'Escape') onClose(); if (e.key === 'ArrowLeft') prev(); if (e.key === 'ArrowRight') next() }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [])

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
      style={{ background: 'rgba(27,42,47,0.92)', backdropFilter: 'blur(8px)' }}
      onClick={onClose}>
      <motion.div initial={{ scale: 0.9 }} animate={{ scale: 1 }} exit={{ scale: 0.9 }}
        onClick={e => e.stopPropagation()} className="relative max-w-2xl w-full">

        {/* Main image */}
        <div className="rounded-2xl overflow-hidden relative">
          <img src={images[idx].url} alt="" className="w-full max-h-[70vh] object-contain"
            style={{ background: '#1a1a1a' }} />
          {images.length > 1 && (
            <>
              {idx > 0 && (
                <button onClick={prev}
                  className="absolute left-3 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full flex items-center justify-center text-white text-lg"
                  style={{ background: 'rgba(27,42,47,0.7)' }} aria-label={t.common.prev}><Icon name="chevronLeft" size={22} strokeWidth={2} /></button>
              )}
              {idx < images.length - 1 && (
                <button onClick={next}
                  className="absolute right-3 top-1/2 -translate-y-1/2 w-10 h-10 rounded-full flex items-center justify-center text-white text-lg"
                  style={{ background: 'rgba(27,42,47,0.7)' }} aria-label={t.common.next}><Icon name="chevronRight" size={22} strokeWidth={2} /></button>
              )}
            </>
          )}
        </div>

        {/* Thumbnails */}
        {images.length > 1 && (
          <div className="flex gap-2 mt-3 justify-center flex-wrap">
            {images.map((img, i) => (
              <button key={img.id} onClick={() => setIdx(i)}
                className="w-14 h-14 rounded-lg overflow-hidden transition-all"
                style={{ border: i === idx ? '2px solid #8FC1B5' : '2px solid transparent', opacity: i === idx ? 1 : 0.5 }}>
                <img src={img.url} alt="" className="w-full h-full object-cover" />
              </button>
            ))}
          </div>
        )}

        {/* Counter */}
        {images.length > 1 && (
          <div className="text-center mt-2 text-sm" style={{ color: '#8FC1B5' }}>
            {idx + 1} / {images.length}
          </div>
        )}

        <button onClick={onClose}
          className="absolute -top-3 -right-3 w-8 h-8 rounded-full flex items-center justify-center text-white text-sm"
          style={{ background: '#2F6F73' }} aria-label={t.common.close}><Icon name="close" size={16} strokeWidth={2.25} /></button>
      </motion.div>
    </motion.div>
  )
}

function ProductCard({ product }) {
  const { t, field } = useLang()
  const [imgIdx, setImgIdx] = useState(0)
  const [lightbox, setLightbox] = useState(false)
  const avail = product.availability || 'AVAILABLE'
  const color = AVAIL_COLORS[avail] || AVAIL_COLORS.AVAILABLE
  const images = product.images || []
  const currentImg = images[imgIdx]
  const name = field(product, 'name')
  const desc = field(product, 'description')

  return (
    <>
      <motion.div
        initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
        transition={{ duration: 0.5 }} whileHover={{ y: -4 }}
        className="rounded-2xl overflow-hidden flex flex-col"
        style={{ background: '#FFFFFF', border: '1px solid #D5DFDB', boxShadow: '0 2px 12px rgba(27,42,47,0.06)' }}
      >
        {/* Main image */}
        <div className="aspect-square overflow-hidden relative" style={{ background: '#E9EFEC' }}>
          {currentImg?.url ? (
            <img src={currentImg.url} alt={name}
              className="w-full h-full object-cover transition-transform duration-500 hover:scale-105 cursor-pointer"
              onClick={() => setLightbox(true)} />
          ) : (
            <div className="w-full h-full flex items-center justify-center" style={{ color: '#566E73' }}><Icon name="vase" size={56} strokeWidth={1.25} /></div>
          )}
          {/* Image count badge */}
          {images.length > 1 && (
            <div className="absolute top-2 right-2 inline-flex items-center gap-1 text-xs px-2 py-0.5 rounded-full text-white"
              style={{ background: 'rgba(27,42,47,0.65)' }}>
              <Icon name="camera" size={13} strokeWidth={2} /> {images.length}
            </div>
          )}
        </div>

        {/* Thumbnail strip */}
        {images.length > 1 && (
          <div className="flex gap-1 px-3 pt-2">
            {images.slice(0, 5).map((img, i) => (
              <button key={img.id} onClick={() => setImgIdx(i)}
                className="w-10 h-10 rounded-md overflow-hidden shrink-0 transition-all"
                style={{ border: i === imgIdx ? '2px solid #2F6F73' : '2px solid transparent', opacity: i === imgIdx ? 1 : 0.55 }}>
                <img src={img.url} alt="" className="w-full h-full object-cover" />
              </button>
            ))}
            {images.length > 5 && (
              <div className="w-10 h-10 rounded-md flex items-center justify-center text-xs"
                style={{ background: '#E9EFEC', color: '#2F6F73' }}>+{images.length - 5}</div>
            )}
          </div>
        )}

        <div className="p-5 flex flex-col flex-1">
          <div className="flex items-start justify-between gap-2 mb-2">
            <h3 className="text-base font-semibold leading-tight" style={{ fontFamily: "'Playfair Display', serif", color: '#1B2A2F' }}>
              {name}
            </h3>
            <span className="text-xs px-2 py-0.5 rounded-full shrink-0" style={{ background: '#E9EFEC', color: '#3E5A60' }}>
              {t.products.categories[product.category] || product.category}
            </span>
          </div>
          {desc && (
            <p className="text-sm mb-3 flex-1 line-clamp-2" style={{ color: '#3E5A60' }}>{desc}</p>
          )}
          <div className="flex items-center justify-between mt-auto pt-3" style={{ borderTop: '1px solid #D5DFDB' }}>
            <div>
              {product.price ? (
                <span className="font-bold text-lg" style={{ color: '#2F6F73' }}>
                  {product.price.toLocaleString('ru-RU')} UZS
                </span>
              ) : (
                <span className="text-sm" style={{ color: '#566E73' }}>{t.products.noPrice}</span>
              )}
              <div className="text-xs mt-0.5 font-medium" style={{ color }}>
                {t.products.availability[avail]}
              </div>
            </div>
            <a href="https://t.me/xxceramics" target="_blank" rel="noreferrer"
              className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg text-sm font-medium text-white transition-all hover:opacity-90"
              style={{ background: '#2F6F73' }}>
              {t.common.contact} <Icon name="arrowRight" size={15} />
            </a>
          </div>
        </div>
      </motion.div>

      <AnimatePresence>
        {lightbox && images.length > 0 && (
          <ImageCarousel images={images} onClose={() => setLightbox(false)} />
        )}
      </AnimatePresence>
    </>
  )
}

export default function Products() {
  const { t } = useLang()
  const [searchParams, setSearchParams] = useSearchParams()
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const category = searchParams.get('category') || ''

  useEffect(() => {
    setLoading(true)
    setPage(0)
    productApi.getAll({ category: category || undefined, page: 0, size: 12 })
      .then(r => { setProducts(r.data.content); setTotalPages(r.data.totalPages) })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [category])

  const loadMore = () => {
    const next = page + 1
    productApi.getAll({ category: category || undefined, page: next, size: 12 })
      .then(r => { setProducts(prev => [...prev, ...r.data.content]); setPage(next) })
      .catch(() => {})
  }

  const setCategory = (val) => {
    if (val) setSearchParams({ category: val })
    else setSearchParams({})
  }

  return (
    <main className="pt-20">
      <section className="py-16 px-4" style={{ background: '#E9EFEC' }}>
        <div className="max-w-7xl mx-auto">
          <SectionTitle as="h1" subtitle={t.products.subtitle} title={t.products.title} description={t.products.desc} />
        </div>
      </section>

      <section className="py-12 px-4" style={{ background: '#F6F8F5' }}>
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-wrap gap-2 mb-10">
            {CATEGORY_KEYS.map(key => (
              <button key={key} onClick={() => setCategory(key)}
                className="px-5 py-2 rounded-full text-sm font-medium transition-all duration-200"
                style={category === key
                  ? { background: '#2F6F73', color: '#F6F8F5' }
                  : { background: '#FFFFFF', color: '#3E5A60', border: '1px solid #D5DFDB' }}>
                {t.products.categories[key]}
              </button>
            ))}
          </div>

          {loading ? <Spinner /> : products.length === 0 ? (
            <div className="text-center py-20" style={{ color: '#566E73' }}>
              <Icon name="package" size={56} strokeWidth={1.25} className="mx-auto mb-4" />
              <p>{t.products.empty}</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {products.map(product => <ProductCard key={product.id} product={product} />)}
              </div>
              {page + 1 < totalPages && (
                <div className="text-center mt-10">
                  <button onClick={loadMore} className="px-8 py-3 rounded-xl text-sm font-medium"
                    style={{ background: '#2F6F73', color: '#F6F8F5' }}>
                    {t.common.loadMore}
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </section>
    </main>
  )
}
