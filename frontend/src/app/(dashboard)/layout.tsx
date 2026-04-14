import Navbar from '@/components/shared/Navbar'
import Header from '@/components/shared/Header'
import AuthGuard from '@/components/shared/AuthGuard'

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthGuard>
      <div className="min-h-screen flex flex-col">
        <Header />
        <div className="flex flex-1">
          <Navbar />
          <main className="flex-1 p-6 bg-secondary/30">{children}</main>
        </div>
      </div>
    </AuthGuard>
  )
}
