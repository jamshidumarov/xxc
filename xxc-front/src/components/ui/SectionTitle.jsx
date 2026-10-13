import { motion } from 'framer-motion'

export default function SectionTitle({ subtitle, title, description, center = false, as: Heading = 'h2' }) {
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
          style={{ color: '#B4552F' }}
        >
          {subtitle}
        </span>
      )}
      <Heading
        className={`font-bold mt-2 ${Heading === 'h1' ? 'text-4xl md:text-5xl' : 'text-3xl md:text-4xl'}`}
        style={{ fontFamily: "'Playfair Display', serif", color: '#1B2A2F' }}
      >
        {title}
      </Heading>
      {description && (
        <p className="mt-4 text-base max-w-xl" style={{ color: '#3E5A60' }}>
          {description}
        </p>
      )}
    </motion.div>
  )
}
