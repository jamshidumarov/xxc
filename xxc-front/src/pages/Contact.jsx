import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { motion } from 'framer-motion'
import { contactApi } from '../lib/api'
import SectionTitle from '../components/ui/SectionTitle'
import { useLang } from '../i18n/index.jsx'
import Icon from '../components/ui/Icon'

const inputStyle = {
  width: '100%', padding: '10px 14px', borderRadius: '10px',
  border: '1px solid #D5DFDB', background: '#FFFFFF', color: '#1B2A2F',
  fontSize: '14px', outline: 'none', transition: 'border-color 0.2s',
}

function Field({ label, error, children, required }) {
  return (
    <div>
      <label className="block text-sm font-medium mb-1.5" style={{ color: '#1B2A2F' }}>
        {label}{required && <span style={{ color: '#B4552F' }}> *</span>}
      </label>
      {children}
      {error && <p className="mt-1 text-xs" style={{ color: '#C0392B' }}>{error}</p>}
    </div>
  )
}

export default function Contact() {
  const { t } = useLang()
  const [files, setFiles] = useState([])
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const schema = z.object({
    name: z.string().min(2, t.contact.validation.name),
    phone: z.string().min(9, t.contact.validation.phone).regex(/^\+?[\d\s\-()]{9,20}$/, t.contact.validation.phone),
    email: z.string().email(t.contact.validation.email).optional().or(z.literal('')),
    type: z.enum(['PURCHASE', 'ORDER', 'PARTNERSHIP', 'OTHER']),
    message: z.string().optional(),
  })

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { type: 'ORDER' },
  })

  const onSubmit = async (data) => {
    setSubmitting(true)
    setError(null)
    try {
      const formData = new FormData()
      formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }))
      files.forEach(f => formData.append('files', f))
      await contactApi.submit(formData)
      setSuccess(true)
      reset()
      setFiles([])
    } catch {
      setError(t.contact.error)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="pt-20">
      <section className="py-16 px-4" style={{ background: '#E9EFEC' }}>
        <div className="max-w-7xl mx-auto">
          <SectionTitle as="h1" subtitle={t.contact.subtitle} title={t.contact.title} description={t.contact.desc} />
        </div>
      </section>

      <section className="py-16 px-4" style={{ background: '#F6F8F5' }}>
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 lg:grid-cols-5 gap-12">
            {/* Form */}
            <div className="lg:col-span-3">
              {success ? (
                <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
                  className="rounded-2xl p-10 text-center"
                  style={{ background: '#FFFFFF', border: '1px solid #D5DFDB' }}>
                  <Icon name="checkCircle" size={56} strokeWidth={1.5} className="mx-auto mb-4" style={{ color: '#4F7D5C' }} />
                  <h3 className="text-2xl font-bold mb-2" style={{ fontFamily: "'Playfair Display', serif", color: '#1B2A2F' }}>
                    {t.contact.successTitle}
                  </h3>
                  <p className="text-sm mb-6" style={{ color: '#3E5A60' }}>{t.contact.successDesc}</p>
                  <button onClick={() => setSuccess(false)} className="px-6 py-2 rounded-xl text-sm font-medium text-white" style={{ background: '#2F6F73' }}>
                    {t.contact.newRequest}
                  </button>
                </motion.div>
              ) : (
                <form onSubmit={handleSubmit(onSubmit)} className="rounded-2xl p-8 space-y-5"
                  style={{ background: '#FFFFFF', border: '1px solid #D5DFDB' }}>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                    <Field label={t.contact.name} error={errors.name?.message} required>
                      <input {...register('name')} placeholder={t.contact.namePlaceholder} style={inputStyle}
                        onFocus={e => (e.target.style.borderColor = '#2F6F73')}
                        onBlur={e => (e.target.style.borderColor = '#D5DFDB')} />
                    </Field>
                    <Field label={t.contact.phone} error={errors.phone?.message} required>
                      <input {...register('phone')} placeholder="+998 90 123 45 67" type="tel" style={inputStyle}
                        onFocus={e => (e.target.style.borderColor = '#2F6F73')}
                        onBlur={e => (e.target.style.borderColor = '#D5DFDB')} />
                    </Field>
                  </div>

                  <Field label={t.contact.email} error={errors.email?.message}>
                    <input {...register('email')} placeholder="email@example.com" type="email" style={inputStyle}
                      onFocus={e => (e.target.style.borderColor = '#2F6F73')}
                      onBlur={e => (e.target.style.borderColor = '#D5DFDB')} />
                  </Field>

                  <Field label={t.contact.type} error={errors.type?.message} required>
                    <select {...register('type')} style={{ ...inputStyle, cursor: 'pointer' }}
                      onFocus={e => (e.target.style.borderColor = '#2F6F73')}
                      onBlur={e => (e.target.style.borderColor = '#D5DFDB')}>
                      {Object.entries(t.contact.types).map(([val, label]) => (
                        <option key={val} value={val}>{label}</option>
                      ))}
                    </select>
                  </Field>

                  <Field label={t.contact.message} error={errors.message?.message}>
                    <textarea {...register('message')} rows={5}
                      style={{ ...inputStyle, resize: 'vertical', minHeight: '120px' }}
                      onFocus={e => (e.target.style.borderColor = '#2F6F73')}
                      onBlur={e => (e.target.style.borderColor = '#D5DFDB')} />
                  </Field>

                  <div>
                    <label className="block text-sm font-medium mb-1.5" style={{ color: '#1B2A2F' }}>
                      {t.contact.attach} <span style={{ color: '#566E73' }}>{t.contact.attachHint}</span>
                    </label>
                    <label className="flex items-center justify-center gap-3 p-5 rounded-xl cursor-pointer transition-colors"
                      style={{ border: '2px dashed #D5DFDB', background: '#F6F8F5', color: '#566E73' }}>
                      <Icon name="paperclip" size={22} />
                      <span className="text-sm">
                        {files.length > 0 ? t.contact.attachSelected(files.length) : t.contact.attachBtn}
                      </span>
                      <input type="file" multiple accept="image/*" className="hidden"
                        onChange={e => setFiles(Array.from(e.target.files).slice(0, 5))} />
                    </label>
                  </div>

                  {error && (
                    <p className="text-sm p-3 rounded-lg" style={{ background: '#FEE2E2', color: '#C0392B' }}>{error}</p>
                  )}

                  <button type="submit" disabled={submitting}
                    className="w-full py-3.5 rounded-xl text-white font-semibold text-base transition-all hover:-translate-y-0.5 disabled:opacity-60"
                    style={{ background: '#2F6F73' }}>
                    {submitting ? t.contact.sending : t.contact.submit}
                  </button>
                </form>
              )}
            </div>

            {/* Contact info */}
            <div className="lg:col-span-2 space-y-6">
              <div className="rounded-2xl p-7" style={{ background: '#FFFFFF', border: '1px solid #D5DFDB' }}>
                <h3 className="text-xl font-bold mb-5" style={{ fontFamily: "'Playfair Display', serif", color: '#1B2A2F' }}>
                  {t.contact.infoTitle}
                </h3>
                <ul className="space-y-4">
                  {t.contact.contactItems.map(c => (
                    <li key={c.label} className="flex items-start gap-3">
                      <span className="w-9 h-9 rounded-lg flex items-center justify-center shrink-0" style={{ background: '#E9EFEC', color: '#2F6F73' }}>
                        <Icon name={c.icon} size={18} />
                      </span>
                      <div>
                        <div className="text-xs font-medium uppercase tracking-wider" style={{ color: '#566E73' }}>{c.label}</div>
                        {c.href ? (
                          <a href={c.href} target={c.href.startsWith('http') ? '_blank' : undefined} rel="noreferrer" className="text-sm font-medium hover:underline" style={{ color: '#2F6F73' }}>{c.value}</a>
                        ) : (
                          <div className="text-sm font-medium" style={{ color: '#1B2A2F' }}>{c.value}</div>
                        )}
                      </div>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="rounded-2xl overflow-hidden" style={{ border: '1px solid #D5DFDB', height: '220px', background: '#E9EFEC' }}>
                <div className="w-full h-full flex flex-col items-center justify-center gap-2" style={{ color: '#566E73' }}>
                  <Icon name="mapPin" size={40} strokeWidth={1.5} />
                  <p className="text-sm">{t.contact.location}</p>
                  <p className="text-xs" style={{ color: '#2F6F73' }}>{t.contact.locationSoon}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  )
}
