import { motion } from 'framer-motion'

export default function SectionTitle({ subtitle, title, description, center = false }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.6 }}
      className={center ? 'text-center' : ''}
    >
      {subtitle && (
        <span
          className="text-sm font-medium uppercase tracking-widest"
          style={{ color: '#D4843C' }}
        >
          {subtitle}
        </span>
      )}
      <h2
        className="text-3xl md:text-4xl font-bold mt-2"
        style={{ fontFamily: "'Playfair Display', serif", color: '#2C1810' }}
      >
        {title}
      </h2>
      {description && (
        <p className="mt-4 text-base max-w-xl" style={{ color: '#6B4A35' }}>
          {description}
        </p>
      )}
    </motion.div>
  )
}
